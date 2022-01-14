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
package org.smartut.localsearch;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.Properties.StoppingCondition;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.sette.LO_Other;

/**
 * Created by J Galeotti on Aug 05 2016.
 */
public class LO_OtherSystemTest extends SystemTestBase {

	@Test
	public void testBase() {

		Properties.LOCAL_SEARCH_RATE = -1; // disable LS/DSE

		String targetClass = LO_Other.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		SmartUt smartut = new SmartUt();
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

	}

	@Before
	public void init() {
		Properties.MINIMIZE = false;
		Properties.ASSERTIONS = false;
		Properties.STOPPING_CONDITION = StoppingCondition.MAXTIME;
		Properties.SEARCH_BUDGET = 30;
		Properties.P_FUNCTIONAL_MOCKING = 0.0d;
		Properties.P_REFLECTION_ON_PRIVATE = 0.0d;
	}

	@Test
	public void testDSE() {

		Properties.LOCAL_SEARCH_PROBABILITY = 1.0;
		Properties.LOCAL_SEARCH_RATE = 8;
		Properties.LOCAL_SEARCH_BUDGET_TYPE = Properties.LocalSearchBudgetType.TESTS;
		Properties.LOCAL_SEARCH_BUDGET = 100;
		Properties.DSE_PROBABILITY = 1.0; // force DSE, not LS
		Properties.DSE_SOLVER = Properties.SolverType.SMARTUT_SOLVER;
		Properties.CONCOLIC_TIMEOUT = Integer.MAX_VALUE; // no timeout

		String targetClass = LO_Other.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		SmartUt smartut = new SmartUt();
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

	}

}
