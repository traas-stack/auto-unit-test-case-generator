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
package org.smartut.basic;

import static org.junit.Assert.assertEquals;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.TargetMethodPrefix;

public class TargetMethodPrefixSystemTest extends SystemTestBase {

	private String targetMethod = "";
	private String targetMethodList = "";
	private String targetMethodPrefix = "";

	@Before
	public void backupValues() {
		targetMethod = Properties.TARGET_METHOD;
		targetMethodList = Properties.TARGET_METHOD_LIST;
		targetMethodPrefix = Properties.TARGET_METHOD_PREFIX;
		Properties.SEARCH_BUDGET = 50000;
	}

	@After
	public void restoreValues() {
		Properties.TARGET_METHOD = targetMethod;
		Properties.TARGET_METHOD_LIST = targetMethodList;
		Properties.TARGET_METHOD_PREFIX = targetMethodPrefix;
	}

	@Test
	public void testTotalBranchesInClass() {
		SmartUt smartut = new SmartUt();

		String targetClass = TargetMethodPrefix.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.TARGET_METHOD = "";
		Properties.TARGET_METHOD_LIST = "";
		Properties.TARGET_METHOD_PREFIX = "";

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function

		assertEquals("Wrong number of goals:", 13, goals);
		assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testTargetMethod() {
		SmartUt smartut = new SmartUt();

		String targetClass = TargetMethodPrefix.class.getCanonicalName();
		String targetMethod = "foo(Ljava/lang/String;Ljava/lang/String;)Z";

		Properties.TARGET_CLASS = targetClass;
		Properties.TARGET_METHOD = targetMethod;
		Properties.TARGET_METHOD_LIST = "";
		Properties.TARGET_METHOD_PREFIX = "";

		String[] command = new String[] { "-generateSuite", "-class", targetClass,
		        "-Dtarget_method=" + targetMethod, "-Dclient_on_thread=true" };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function

		assertEquals("Wrong number of goals: ", 4, goals);
		assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

	}

	@Test
	public void testTargetMethodPrefix1() {
		SmartUt smartut = new SmartUt();

		String targetClass = TargetMethodPrefix.class.getCanonicalName();
		String targetMethodPrefix = "foo_";

		Properties.TARGET_CLASS = targetClass;
		Properties.TARGET_METHOD = "";
		Properties.TARGET_METHOD_LIST = "";
		Properties.TARGET_METHOD_PREFIX = targetMethodPrefix;

		String[] command = new String[] { "-generateSuite", "-class", targetClass,
		        "-Dtarget_method_prefix=" + targetMethodPrefix, "-Dclient_on_thread=true" };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function

		assertEquals("Wrong number of goals: ", 4, goals);
		assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

	}

	@Test
	public void testTargetMethodPrefix2() {
		SmartUt smartut = new SmartUt();

		String targetClass = TargetMethodPrefix.class.getCanonicalName();
		String targetMethodPrefix = "foo"; // different from foo_

		Properties.TARGET_CLASS = targetClass;
		Properties.TARGET_METHOD = "";
		Properties.TARGET_METHOD_LIST = "";
		Properties.TARGET_METHOD_PREFIX = targetMethodPrefix;

		String[] command = new String[] { "-generateSuite", "-class", targetClass,
		        "-Dtarget_method_prefix=" + targetMethodPrefix, "-Dclient_on_thread=true" };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function

		assertEquals("Wrong number of goals: ", 8, goals);
		assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

		Properties.TARGET_METHOD_PREFIX = "";
	}
}
