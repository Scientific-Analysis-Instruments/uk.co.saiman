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
package uk.co.saiman.text;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theory;

import uk.co.saiman.utility.Enumeration;

/**
 * Careful design is necessary here, as in some cases we are testing what
 * happens during class initialisation, so extra thought must be put into test
 * isolation
 * 
 * @author Elias N Vasylenko
 *
 */
@SuppressWarnings("javadoc")
public class StringEscaperTest {
	@DataPoint
	public static final String SENTENCE = "The small brown dog.";

	/**
	 * Confirm that {@link Enumeration}s are properly initialised when accessed
	 * first by literal, rather than via {@link Enumeration#getConstants(Class)}.
	 * This is significant because of the odd initialisation logic to enforce
	 * instantiation only inside static initialisers.
	 */
	@Theory
	public void testEnumLiteralAccess() {}

	/**
	 * Confirm that an {@link Enumeration} works as an inner class (implementation
	 * may perform stack trace examination).
	 */
	@Theory
	public void testInnerEnum() {}
}