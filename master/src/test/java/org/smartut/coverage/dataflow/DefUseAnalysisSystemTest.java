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
package org.smartut.coverage.dataflow;

import java.util.Arrays;

import com.examples.with.different.packagename.DataUtils;
import com.examples.with.different.packagename.defuse.DefUseExample2;
import com.examples.with.different.packagename.defuse.DefUseExample3;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.defuse.DefUseExample1;
import com.examples.with.different.packagename.defuse.GCD;

public class DefUseAnalysisSystemTest extends SystemTestBase {

    private final Criterion[] oldCriterion = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
	private final boolean oldAssertions = Properties.ASSERTIONS;
	private final boolean DEFAULT_SANDBOX = Properties.SANDBOX;
	private final String analysisCriteria = Properties.ANALYSIS_CRITERIA;

	@Before
	public void beforeTest() {
		Properties.SANDBOX = true;
		Properties.CRITERION = new Criterion[] { Criterion.DEFUSE };
		//Properties.ANALYSIS_CRITERIA = "Branch,DefUse";
		Properties.TARGET_CLASS = DefUseExample1.class.getCanonicalName();
	}

	@After
	public void afterTest() {
		Properties.CRITERION = oldCriterion;
		Properties.ASSERTIONS = oldAssertions;
		Properties.SANDBOX = DEFAULT_SANDBOX;
		Properties.ANALYSIS_CRITERIA = analysisCriteria;
	}

	@Test
	public void testSimpleExample() {
		SmartUt smartut = new SmartUt();

		String targetClass = DefUseExample1.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;

		// Need to deactivate assertions, otherwise classloader is chanaged 
		// and DefUseCoverageFactory is reset
		Properties.ASSERTIONS = false;
		//Properties.ANALYSIS_CRITERIA = "Branch,DefUse";

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals(0, DefUseCoverageFactory.getInterMethodGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getIntraClassGoalsCount());
		Assert.assertEquals(1, DefUseCoverageFactory.getParamGoalsCount());
		Assert.assertEquals(3, DefUseCoverageFactory.getIntraMethodGoalsCount());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testGCDExample() {
		SmartUt smartut = new SmartUt();

		String targetClass = GCD.class.getCanonicalName();

		Properties.ASSERTIONS = false;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		
		System.out.println("Def: "+DefUsePool.getDefCounter());
		//DefUseCoverageFactory.computeGoals();
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		Assert.assertEquals(0, DefUseCoverageFactory.getInterMethodGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getIntraClassGoalsCount());
		Assert.assertEquals(4, DefUseCoverageFactory.getParamGoalsCount()); // 3 or 4?
		Assert.assertEquals(6, DefUseCoverageFactory.getIntraMethodGoalsCount());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testDefUseExample2() {
		SmartUt smartut = new SmartUt();

		String targetClass = DefUseExample2.class.getCanonicalName();

		Properties.ASSERTIONS = false;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		System.out.println("Def: "+DefUsePool.getDefCounter());
		//DefUseCoverageFactory.computeGoals();
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		Assert.assertEquals(0, DefUseCoverageFactory.getInterMethodGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getIntraClassGoalsCount());
		Assert.assertEquals(1, DefUseCoverageFactory.getParamGoalsCount());
		Assert.assertEquals(3, DefUseCoverageFactory.getIntraMethodGoalsCount());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testDefUseIntraClassPairs() {
		SmartUt smartut = new SmartUt();

		String targetClass = DefUseExample3.class.getCanonicalName();

		Properties.ASSERTIONS = false;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		System.out.println("Def: "+DefUsePool.getDefCounter());
		//DefUseCoverageFactory.computeGoals();
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		Assert.assertEquals(1, DefUseCoverageFactory.getInterMethodGoalsCount());
		Assert.assertEquals(1, DefUseCoverageFactory.getIntraClassGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getParamGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getIntraMethodGoalsCount());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void test2DArrayInstrumentation() {
		SmartUt smartut = new SmartUt();
		String targetClass = DataUtils.class.getCanonicalName();
		Properties.ASSERTIONS = false;
		String[] command = new String[]{"-generateSuite", "-class", targetClass};
		Object result = smartut.parseCommandLine(command);

		System.out.println("Def: "+DefUsePool.getDefCounter());
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.print("EvolvedTestSuite:\n" + best.toString());
		Assert.assertEquals(0, DefUseCoverageFactory.getInterMethodGoalsCount());
		Assert.assertEquals(0, DefUseCoverageFactory.getIntraClassGoalsCount());
		Assert.assertEquals(3, DefUseCoverageFactory.getParamGoalsCount());
		//Assert.assertEquals(0, DefUseCoverageFactory.getIntraMethodGoalsCount()); Difficult to expect the correct "expected" value as the method in CUT has a loop.
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);

	}
}
