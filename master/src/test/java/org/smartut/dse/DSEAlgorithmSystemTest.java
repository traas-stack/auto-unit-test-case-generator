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
package org.smartut.dse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.Properties.SolverType;
import org.smartut.Properties.StoppingCondition;
import org.smartut.Properties.Strategy;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.dse.Add;
import com.examples.with.different.packagename.dse.ArrayLengthExample;
import com.examples.with.different.packagename.dse.BooleanExample;
import com.examples.with.different.packagename.dse.ByteExample;
import com.examples.with.different.packagename.dse.CharExample;
import com.examples.with.different.packagename.dse.DoubleExample;
import com.examples.with.different.packagename.dse.FloatExample;
import com.examples.with.different.packagename.dse.LongExample;
import com.examples.with.different.packagename.dse.Max;
import com.examples.with.different.packagename.dse.Min;
import com.examples.with.different.packagename.dse.MinUnreachableCode;
import com.examples.with.different.packagename.dse.NoStaticMethod;
import com.examples.with.different.packagename.dse.ObjectExample;
import com.examples.with.different.packagename.dse.ShortExample;
import com.examples.with.different.packagename.dse.StringExample;

public class DSEAlgorithmSystemTest extends SystemTestBase {

	@Before
	public void init() {
		Properties.VIRTUAL_FS = true;
		Properties.VIRTUAL_NET = true;
		Properties.LOCAL_SEARCH_PROBABILITY = 1.0;
		Properties.LOCAL_SEARCH_RATE = 1;
		Properties.LOCAL_SEARCH_BUDGET_TYPE = Properties.LocalSearchBudgetType.TESTS;
		Properties.LOCAL_SEARCH_BUDGET = 100;
		Properties.SEARCH_BUDGET = 50000;
		// Properties.CONCOLIC_TIMEOUT = Integer.MAX_VALUE;
		Properties.RESET_STATIC_FIELD_GETS = true;

		String cvc4_path = System.getenv("cvc4_path");
		if (cvc4_path != null) {
			Properties.CVC4_PATH = cvc4_path;
		}

		Properties.DSE_SOLVER = SolverType.CVC4_SOLVER;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXTIME;
		Properties.SEARCH_BUDGET = 60 * 60 * 10; // 10 hours
		Properties.MINIMIZATION_TIMEOUT = 60 * 60;
		Properties.ASSERTION_TIMEOUT = 60 * 60;
		// Properties.TIMEOUT = Integer.MAX_VALUE;

		Properties.STRATEGY = Strategy.DSE;

		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		Properties.MINIMIZE = true;
		Properties.ASSERTIONS = true;

		assumeTrue(Properties.CVC4_PATH != null);
	}

	@Test
	public void testMax() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(7, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());
	}

	@Test
	public void testAdd() {
		SmartUt smartut = new SmartUt();
		String targetClass = Add.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object results = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(results);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(1, best.getTests().size());

		assertEquals(1, best.getNumOfCoveredGoals());
		assertEquals(1, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testNoStaticMethod() {
		SmartUt smartut = new SmartUt();
		String targetClass = NoStaticMethod.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object results = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(results);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		assertTrue(best.getTests().isEmpty());

		System.out.println("EvolvedTestSuite:\n" + best);

		assertTrue(best.getTests().isEmpty());

		assertEquals(0, best.getNumOfCoveredGoals());
		assertEquals(1, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testMin() {

		SmartUt smartut = new SmartUt();
		String targetClass = Min.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(2, best.getNumOfCoveredGoals());
		assertEquals(1, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testBooleanInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = BooleanExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testShortInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = ShortExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testByteInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = ByteExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testCharInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = CharExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testLongInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = LongExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testDoubleInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = DoubleExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testFloatInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = FloatExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());

	}

	@Test
	public void testUnreachableCode() {

		SmartUt smartut = new SmartUt();
		String targetClass = MinUnreachableCode.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertEquals(2, best.getTests().size());

		assertEquals(3, best.getNumOfCoveredGoals());
		assertEquals(2, best.getNumOfNotCoveredGoals());
	}

	@Test
	public void testMaxTestsStoppingCondition() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXTESTS;
		Properties.SEARCH_BUDGET = 1;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(1, best.getTests().size());
	}

	@Test
	public void testMaxFitnessEvaluationStoppingCondition() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXFITNESSEVALUATIONS;
		Properties.SEARCH_BUDGET = 2;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(1, best.getTests().size());
	}

	@Test
	public void testMaxTimeStoppingCondition() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXTIME;
		Properties.SEARCH_BUDGET = -1;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertTrue(best.getTests().isEmpty());

	}

	@Test
	public void testMaxStatementsStoppingCondition() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXSTATEMENTS;
		Properties.SEARCH_BUDGET = 1;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(1, best.getTests().size());
	}

	@Test
	public void testStopZeroMax() {

		SmartUt smartut = new SmartUt();
		String targetClass = Max.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.STOP_ZERO = true;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(7, best.getNumOfCoveredGoals());
		assertEquals(0, best.getNumOfNotCoveredGoals());
	}

	@Test
	public void testObjectInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = ObjectExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertTrue(best.getNumOfCoveredGoals() >= 3);

	}

	@Test
	public void testStringInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = StringExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());
		assertTrue(best.getTests().size() >= 2);

		assertTrue(best.getNumOfCoveredGoals() >= 4);

	}

	@Test
	public void testArrayLength() {

		SmartUt smartut = new SmartUt();
		String targetClass = ArrayLengthExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertTrue(best.getNumOfCoveredGoals() >= 3);

	}

}
