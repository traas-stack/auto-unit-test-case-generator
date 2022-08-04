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
package org.smartut.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.smartut.junit.examples.AbstractJUnit3Test;
import org.smartut.junit.examples.JUnit3Suite;
import org.smartut.junit.examples.JUnit3Test;
import org.smartut.junit.examples.JUnit4Categories;
import org.smartut.junit.examples.JUnit4Suite;
import org.smartut.junit.examples.JUnit4SmartUtTest;
import org.smartut.junit.examples.JUnit4Test;
import org.smartut.junit.examples.Not_A_Test;
import org.smartut.junit.examples.JUnit4ParameterizedTest;
import org.junit.Test;

public class CoverageAnalysisTest {

	@Test
	public void isTest() {
		assertFalse(CoverageAnalysis.isTest(Not_A_Test.class));

		assertTrue(CoverageAnalysis.isTest(JUnit3Test.class));
		assertFalse(CoverageAnalysis.isTest(JUnit3Suite.class));
		assertFalse(CoverageAnalysis.isTest(AbstractJUnit3Test.class));

		assertTrue(CoverageAnalysis.isTest(JUnit4Test.class));
		assertTrue(CoverageAnalysis.isTest(JUnit4SmartUtTest.class));
		assertFalse(CoverageAnalysis.isTest(JUnit4Suite.class));
		assertFalse(CoverageAnalysis.isTest(JUnit4Categories.class));
		assertTrue(CoverageAnalysis.isTest(JUnit4ParameterizedTest.class));
	}
}
