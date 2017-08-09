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
 * This file is part of uk.co.saiman.mathematics.
 *
 * uk.co.saiman.mathematics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.mathematics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.mathematics.graph.impl;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import uk.co.saiman.mathematics.graph.Graph;
import uk.co.saiman.mathematics.graph.GraphTransformer;

public class GraphTransformerImpl<V, E> implements GraphTransformer<V, E> {
	private final Graph<V, E> source;

	public GraphTransformerImpl(Graph<V, E> source) {
		this.source = source;
	}

	@Override
	public Graph<V, E> create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <W> GraphTransformer<W, E> vertices(Function<V, W> transformation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <F> GraphTransformer<V, F> edges(Function<E, F> transformation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphTransformer<V, E> edgeWeight(Function<E, Double> weight,
			boolean mutable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphTransformer<V, E> direction(Comparator<V> lowToHigh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <W> GraphTransformer<W, E> filterVertices(Predicate<V> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <W> GraphTransformer<W, E> filterEdges(Predicate<E> filter) {
		// TODO Auto-generated method stub
		return null;
	}
}