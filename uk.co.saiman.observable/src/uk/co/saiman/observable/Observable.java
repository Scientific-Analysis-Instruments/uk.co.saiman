/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.observable.Observer.onCompletion;
import static uk.co.saiman.observable.Observer.onObservation;
import static uk.co.saiman.observable.Observer.singleUse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import uk.co.saiman.utility.IdentityProperty;
import uk.co.saiman.utility.Property;

/**
 * Simple interface for an observable object, with methods to add and remove
 * observers expecting the applicable type of message.
 * 
 * @author Elias N Vasylenko
 * @param <M>
 *          The message type. This may be {@link Void} if no message need be
 *          sent.
 */
public interface Observable<M> {
  /**
   * Observers added will receive messages from this Observable.
   * 
   * @param observer
   *          an observer to add
   * @return a disposable over the observation
   */
  Disposable observe(Observer<? super M> observer);

  /**
   * As {@link #observe(Observer)} with an empty observer.
   * 
   * @return a disposable over the observation
   */
  default Disposable observe() {
    return observe(m -> {});
  }

  /**
   * Block until we either receive the next message event of the next failure
   * event. In the case of the former, return it, and in the case of the latter,
   * throw a {@link MissingValueException}.
   * 
   * @return the next value
   * @throws MissingValueException
   *           If a failure or completion event is received before the next
   *           message event. In the former case the cause will be the failure
   *           throwable, in the latter case an instance of
   *           {@link AlreadyCompletedException}.
   */
  default CompletableFuture<M> getNext() {
    CompletableFuture<M> result = new CompletableFuture<>();

    thenAfter(onObservation(o -> o.requestUnbounded())).observe(singleUse(o -> new Observer<M>() {
      @Override
      public void onComplete() {
        result.completeExceptionally(new AlreadyCompletedException(o));
      }

      @Override
      public void onFail(Throwable t) {
        result.completeExceptionally(t);
      }

      @Override
      public void onNext(M message) {
        o.cancel();
        result.complete(message);
      }
    }));

    return result;
  }

  /**
   * Block until we either receive the next message event of the next failure
   * event. In the case of the former, return it, and in the case of the latter,
   * throw a {@link MissingValueException}.
   * 
   * @return the next value
   * @throws MissingValueException
   *           If a failure or completion event is received before the next
   *           message event. In the former case the cause will be the failure
   *           throwable, in the latter case an instance of
   *           {@link AlreadyCompletedException}.
   */
  default M get() {
    CompletableFuture<M> result = getNext();

    try {
      return result.get();
    } catch (ExecutionException e) {
      throw new MissingValueException(this, e);
    } catch (Exception e) {
      throw new RuntimeException("Unexpected error", e);
    }
  }

  default Optional<M> tryGet() {
    try {
      return Optional.of(get());
    } catch (MissingValueException e) {
      return Optional.empty();
    }
  }

  /**
   * Derive a new observable by application of the given function. This gives the
   * same result as just applying the function to the observable directly, and
   * exists only to create a more natural fit into the fluent API by making the
   * order of operations clearer in method chains.
   * 
   * @param <T>
   *          the type of the resulting observable
   * @param transformation
   *          the transformation function to apply to the observable
   * @return the derived observable
   */
  default <T> Observable<T> compose(Function<Observable<M>, Observable<T>> transformation) {
    return transformation.apply(this);
  }

  /**
   * A collector which can be applied to a {@link Stream} to derive a cold
   * observable.
   * 
   * @param <M>
   *          the type of the observable
   * @return an observable over the given stream
   */
  public static <M> Collector<M, ?, Observable<M>> toObservable() {
    return collectingAndThen(toList(), Observable::of);
  }

  /**
   * Derive an observable which passes events to the given observer directly
   * before passing them downstream.
   * 
   * @param action
   *          an observer representing the action to take
   * @return an observable which performs the injected behavior
   */
  default Observable<M> then(Observer<M> action) {
    return observer -> observe(new MultiplePassthroughObserver<>(observer, action));
  }

  /**
   * Derive an observable which passes events to the given observer directly after
   * passing them downstream.
   * 
   * @param action
   *          an observer representing the action to take
   * @return an observable which performs the injected behavior
   */
  default Observable<M> thenAfter(Observer<M> action) {
    return observer -> observe(new MultiplePassthroughObserver<>(action, observer));
  }

  default Observable<M> retrying() {
    return observer -> observe(new RetryingObserver<>(observer, this));
  }

  /*
   * TODO refactor this so it works from an Observable<? extends Observable<?
   * extends M>>
   */
  default Observable<M> retrying(Observable<? extends M> retryOn) {
    return observer -> observe(new RetryingObserver<>(observer, retryOn));
  }

  default Observable<ObservableValue<M>> materialize() {
    return observer -> observe(new MaterializingObserver<>(observer));
  }

  default ObservableValue<M> toValue() {
    return toValue(new MissingValueException(this));
  }

  default ObservableValue<M> toValue(M initial) {
    initial = getNext().getNow(initial);
    return new ObservablePropertyImpl<M>(initial);
  }

  default ObservableValue<M> toValue(Throwable initialProblem) {
    M initial = getNext().getNow(null);
    if (initial == null) {
      return new ObservablePropertyImpl<>(initial);
    } else {
      return new ObservablePropertyImpl<>(initialProblem);
    }
  }

  /**
   * Derive an observable which automatically disposes of observers at some point
   * after they are no longer weakly reachable.
   * 
   * @return the derived observable
   */
  default Observable<M> weakReference() {
    return observer -> observe(ReferenceObserver.weak(observer));
  }

  /**
   * Derive an observable which automatically disposes of observers at some point
   * after the given owner is no longer weakly reachable.
   * <p>
   * Care should be taken not to refer to the owner directly in any observer
   * logic, as this will create a strong reference to the owner, preventing it
   * from becoming unreachable. For this reason, the message is transformed into
   * an {@link OwnedMessage}, which may create references to the owner on demand
   * within observer logic without retainment.
   * 
   * @param <O>
   *          the type of the owning object
   * @param owner
   *          the owning referent object
   * @return the derived observable
   */
  default <O> Observable<OwnedMessage<O, M>> weakReference(O owner) {
    return observer -> observe(ReferenceOwnedObserver.weak(owner, observer));
  }

  /**
   * Derive an observable which automatically disposes of observers at some point
   * after they are no longer softly reachable.
   * 
   * @return the derived observable
   */
  default Observable<M> softReference() {
    return observer -> observe(ReferenceObserver.soft(observer));
  }

  /**
   * Derive an observable which automatically disposes of observers at some point
   * after the given owner is no longer softly reachable.
   * <p>
   * Care should be taken not to refer to the owner directly in any observer
   * logic, as this will create a strong reference to the owner, preventing it
   * from becoming unreachable. For this reason, the message is transformed into
   * an {@link OwnedMessage}, which may create references to the owner on demand
   * within observer logic without retainment.
   * 
   * @param <O>
   *          the type of the owning object
   * @param owner
   *          the owning referent object
   * @return the derived observable
   */
  default <O> Observable<OwnedMessage<O, M>> softReference(O owner) {
    return observer -> observe(ReferenceOwnedObserver.soft(owner, observer));
  }

  /**
   * Derive an observable which re-emits messages on the given executor.
   * 
   * @param executor
   *          the target executor
   * @return the derived observable
   */
  default Observable<M> executeOn(Executor executor) {
    return observer -> observe(new ExecutorObserver<>(observer, executor));
  }

  /**
   * Derive an observable which transforms messages according to the given
   * mapping.
   * 
   * @param <T>
   *          the type of the derived observable
   * @param mapping
   *          the mapping function
   * @return an observable over the mapped messages
   */
  default <T> Observable<T> map(Function<? super M, ? extends T> mapping) {
    return observer -> observe(new MappingObserver<>(observer, mapping));
  }

  /**
   * Derive an observable which passes along only those messages which match the
   * given condition.
   * 
   * @param condition
   *          the terminating condition
   * @return the derived observable
   */
  default Observable<M> filter(Predicate<? super M> condition) {
    return observer -> observe(new FilteringObserver<>(observer, condition));
  }

  /**
   * Derive an observable which completes and disposes itself after receiving a
   * message which matches the given condition.
   * 
   * @param condition
   *          the terminating condition
   * @return the derived observable
   */
  default Observable<M> takeWhile(Predicate<? super M> condition) {
    return observer -> observe(new TakeWhileObserver<>(observer, condition));
  }

  /**
   * Derive an observable which completes and disposes itself after receiving a
   * message which matches the given condition.
   * 
   * @param condition
   *          the terminating condition
   * @return the derived observable
   */
  default Observable<M> dropWhile(Predicate<? super M> condition) {
    return observer -> observe(new DropWhileObserver<>(observer, condition));
  }

  /**
   * Derive an observable which maps each message to an intermediate observable,
   * then merges the messages from the intermediate observables into a single
   * source.
   * <p>
   * An unbounded request is made to the upstream observable, so it is not
   * required to support backpressure.
   * <p>
   * The intermediate observables are not required to support backpressure, as an
   * unbounded request will be made to them and the downstream observable will
   * forward every message as soon as it is available. Because of this, The
   * downstream observable does not support backpressure.
   * 
   * @param <T>
   *          the resulting observable message type
   * 
   * @param mapping
   *          the terminating condition
   * @return the derived observable
   */
  default <T> Observable<T> mergeMap(
      Function<? super M, ? extends Observable<? extends T>> mapping) {
    throw new UnsupportedOperationException(); // TODO
  }

  /**
   * Introduce backpressure by mapping each message to an intermediate observable
   * which supports backpressure and then interleaving these observables
   * downstream.
   * <p>
   * An unbounded request is made to the upstream observable, so it is not
   * required to support backpressure.
   * <p>
   * The intermediate observables must support backpressure. Priority for
   * forwarding requests to intermediate observables is determined as follows:
   * fewest outstanding requests, then fewest total requests, then first taken
   * from upstream.
   * 
   * @param <T>
   *          the resulting observable message type
   * 
   * @param mapping
   *          the terminating condition
   * @return the derived observable
   */
  default <T> Observable<T> interleaveMap(
      Function<? super M, ? extends Observable<? extends T>> mapping) {
    throw new UnsupportedOperationException(); // TODO
  }

  /**
   * Derive an observable which sequentially maps each message to an intermediate
   * observable.
   * <p>
   * The intermediate accepts observations from downstream until it is complete,
   * at which point the next message is requested from upstream and the process is
   * repeated.
   * <p>
   * The upstream and intermediate observables must both support backpressure.
   * 
   * @param <T>
   *          the resulting observable message type
   * 
   * @param mapping
   *          the terminating condition
   * @return the derived observable
   */
  default <T> Observable<T> flatMap(
      Function<? super M, ? extends Observable<? extends T>> mapping) {
    throw new UnsupportedOperationException(); // TODO
  }

  default <R> CompletableFuture<R> reduce(
      Supplier<R> identity,
      BiFunction<R, ? super M, R> accumulator) {
    CompletableFuture<R> future = new CompletableFuture<>();

    Property<Observation> observation = new IdentityProperty<>();
    this
        .thenAfter(onCompletion(() -> observation.get().requestNext()))
        .reduceBackpressure(identity, accumulator)
        .then(onObservation(o -> observation.set(o)))
        .observe(m -> future.complete(m));

    return future;
  }

  /**
   * Introduce backpressure by reducing messages until a request is made
   * downstream, then forwarding the reduction.
   * 
   * @param <R>
   *          the resulting reduction type
   * 
   * @param identity
   *          the identity value for the accumulating function
   * @param accumulator
   *          an associative, non-interfering, stateless function for combining
   *          two values
   * @return an observable over the reduced values
   */
  default <R> Observable<R> reduceBackpressure(
      Supplier<? extends R> identity,
      BiFunction<? super R, ? super M, ? extends R> accumulator) {
    return observer -> observe(new BackpressureReducingObserver<>(observer, identity, accumulator));
  }

  /**
   * Introduce backpressure by reducing messages until a request is made
   * downstream, then forwarding the reduction.
   * 
   * @param <R>
   *          the resulting reduction type
   * 
   * @param initial
   *          the initial value for the accumulating function
   * @param accumulator
   *          an associative, non-interfering, stateless function for combining
   *          two values
   * @return an observable over the reduced values
   */
  default <R> Observable<R> reduceBackpressure(
      Function<? super M, ? extends R> initial,
      BiFunction<? super R, ? super M, ? extends R> accumulator) {
    return observer -> observe(new BackpressureReducingObserver<>(observer, initial, accumulator));
  }

  /**
   * Introduce backpressure by reducing messages until a request is made
   * downstream, then forwarding the reduction.
   * 
   * @param accumulator
   *          an associative, non-interfering, stateless function for combining
   *          two values
   * @return an observable over the reduced values
   */
  default Observable<M> reduceBackpressure(BinaryOperator<M> accumulator) {
    return reduceBackpressure(m -> m, accumulator);
  }

  default <R, A> CompletableFuture<R> collect(Collector<? super M, A, ? extends R> collector) {
    return reduce(collector.supplier(), (a, m) -> {
      collector.accumulator();
      return a;
    }).thenApply(collector.finisher());
  }

  /**
   * Introduce backpressure by collecting messages until a request is made
   * downstream, then forwarding the collection.
   * 
   * @param <R>
   *          the resulting collection type
   * @param <A>
   *          the intermediate collection type
   * 
   * @param collector
   *          the collector to apply to incoming messages
   * @return an observable over the collected values
   */
  default <R, A> Observable<R> collectBackpressure(Collector<? super M, A, ? extends R> collector) {
    return reduceBackpressure(collector.supplier(), (a, m) -> {
      collector.accumulator();
      return a;
    }).map(collector.finisher());
  }

  default Observable<List<M>> aggregateBackpressure() {
    return aggregateBackpressure(256);
  }

  default Observable<List<M>> aggregateBackpressure(long toCapacity) {
    return collectBackpressure(toCollection(() -> new MaximumCapacityList<>(toCapacity)));
  }

  /*
   * Static factories
   */

  @SafeVarargs
  public static <M> Observable<M> of(M... messages) {
    return of(Arrays.asList(messages));
  }

  public static <M> Observable<M> of(Collection<? extends M> messages) {
    return new ColdObservable<>(messages);
  }

  @SafeVarargs
  public static <M> Observable<M> merge(Observable<? extends M>... observables) {
    return merge(Arrays.asList(observables));
  }

  public static <M> Observable<M> merge(Collection<? extends Observable<? extends M>> observables) {
    return of(observables).mergeMap(identity());
  }
}