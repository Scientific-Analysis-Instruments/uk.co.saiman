/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.text.
 *
 * uk.co.saiman.text is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.text is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.text.parsing;

import java.util.function.BiFunction;

public class AppendingParser<T, U> implements AbstractParser<T> {
	private final Parser<T> main;
	private final Parser<U> append;

	private final BiFunction<T, U, ? extends T> combinor;

	public AppendingParser(Parser<T> main, Parser<U> append,
			BiFunction<T, U, ? extends T> combinor) {
		this.main = main;
		this.append = append;

		this.combinor = combinor;
	}

	@Override
	public ParseResult<T> parseSubstringImpl(ParseState state) {
		ParseResult<T> mainValue = main.parseSubstring(state.toEnd(false));
		ParseResult<U> appendValue;

		try {
			appendValue = append.parseSubstring(mainValue.state()
					.toEnd(state.toEnd()));
		} catch (ParseException e) {
			return mainValue.mapState(s -> s.addException(e)).mapState(
					s -> s.toEnd(state.toEnd()));
		} catch (Exception e) {
			return mainValue.mapState(s -> s.toEnd(state.toEnd()));
		}

		return appendValue.mapState(s -> s.addException(mainValue.state()))
				.mapResult(a -> combinor.apply(mainValue.result(), a));
	}

	@Override
	public String toString() {
		return "Appending Parser [" + main + " > " + append + "]";
	}
}
