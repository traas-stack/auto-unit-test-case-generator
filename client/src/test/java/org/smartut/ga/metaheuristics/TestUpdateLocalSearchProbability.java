/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * Copyright (C) 2021- SmartUt contributors
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */
package org.smartut.ga.metaheuristics;

import static org.junit.Assert.*;

import org.smartut.Properties;
import org.junit.Test;

public class TestUpdateLocalSearchProbability {

	private static final double DELTA = 0.0000000001;

	@Test
	public void testNoChanges() {
		Properties.LOCAL_SEARCH_PROBABILITY = 0.5;
		Properties.LOCAL_SEARCH_ADAPTATION_RATE = 2.0;

		MonotonicGA<?> ga = new MonotonicGA<>(null);
		assertEquals(0.5, ga.localSearchProbability, DELTA);
	}

	@Test
	public void testDecrease() {
		Properties.LOCAL_SEARCH_PROBABILITY = 1.0;
		Properties.LOCAL_SEARCH_ADAPTATION_RATE = 2.0;

		MonotonicGA<?> ga = new MonotonicGA<>(null);
		assertEquals(1.0, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.5, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.25, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.125, ga.localSearchProbability, DELTA);

	}

	@Test
	public void testIncrease() {
		Properties.LOCAL_SEARCH_PROBABILITY = 0.125;
		Properties.LOCAL_SEARCH_ADAPTATION_RATE = 4.0;

		MonotonicGA<?> ga = new MonotonicGA<>(null);
		assertEquals(0.125, ga.localSearchProbability, DELTA);

		ga.updateProbability(true);
		assertEquals(0.5, ga.localSearchProbability, DELTA);

		ga.updateProbability(true);
		assertEquals(1.0, ga.localSearchProbability, DELTA);

	}
	
	@Test
	public void testIncreaseAndDecrease() {
		Properties.LOCAL_SEARCH_PROBABILITY = 0.5;
		Properties.LOCAL_SEARCH_ADAPTATION_RATE = 2.0;

		MonotonicGA<?> ga = new MonotonicGA<>(null);
		assertEquals(0.5, ga.localSearchProbability, DELTA);

		ga.updateProbability(true);
		assertEquals(1.0, ga.localSearchProbability, DELTA);

		ga.updateProbability(true);
		assertEquals(1.0, ga.localSearchProbability, DELTA);

		ga.updateProbability(true);
		assertEquals(1.0, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.5, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.25, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.125, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.0625, ga.localSearchProbability, DELTA);

		ga.updateProbability(false);
		assertEquals(0.03125, ga.localSearchProbability, DELTA);

	}

}
