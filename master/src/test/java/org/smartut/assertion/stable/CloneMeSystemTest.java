/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and SmartUt
 * contributors
 *
 * This file is part of SmartUt.
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
package org.smartut.assertion.stable;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testcase.TestCase;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.stable.CloneMe;

public class CloneMeSystemTest extends SystemTestBase {
	private final boolean DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS = Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS;
	private final boolean DEFAULT_REPLACE_CALLS = Properties.REPLACE_CALLS;
	private final Properties.JUnitCheckValues DEFAULT_JUNIT_CHECK = Properties.JUNIT_CHECK;
	private final boolean DEFAULT_JUNIT_TESTS = Properties.JUNIT_TESTS;
	private final boolean DEFAULT_PURE_INSPECTORS = Properties.PURE_INSPECTORS;
	private final boolean DEFAULT_SANDBOX = Properties.SANDBOX;
	

	@Before
	public void configureProperties() {
		Properties.SANDBOX = true;
		Properties.RESET_STATIC_FIELDS = true;
		Properties.REPLACE_CALLS = true;
		Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
		Properties.JUNIT_TESTS = true;
		Properties.PURE_INSPECTORS = true;
		Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = false;

	}

	@After
	public void restoreProperties() {
		Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS;
		Properties.SANDBOX = DEFAULT_SANDBOX;
		Properties.REPLACE_CALLS = DEFAULT_REPLACE_CALLS;
		Properties.JUNIT_CHECK = DEFAULT_JUNIT_CHECK;
		Properties.JUNIT_TESTS = DEFAULT_JUNIT_TESTS;
		Properties.PURE_INSPECTORS = DEFAULT_PURE_INSPECTORS;
	}

	@Test 
	public void testCloneMe() {
		SmartUt smartut = new SmartUt();

		String targetClass = CloneMe.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		List<TestCase> tests = best.getTests();
		boolean allStable = TestStabilityChecker.checkStability(tests);
		assertTrue(allStable);
	}

}
