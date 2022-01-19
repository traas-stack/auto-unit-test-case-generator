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

import com.examples.with.different.packagename.solver.MazeClient;

public class DSEMazeSystemTest extends SystemTestBase {

	@Before
	public void init() {
		Properties.VIRTUAL_FS = true;
		Properties.VIRTUAL_NET = true;
		Properties.SEARCH_BUDGET = 50000;
		// Properties.CONCOLIC_TIMEOUT = Integer.MAX_VALUE;
		Properties.RESET_STATIC_FIELD_GETS = true;

		String cvc4_path = System.getenv("cvc4_path");
		if (cvc4_path != null) {
			Properties.CVC4_PATH = cvc4_path;
		}

		Properties.DSE_SOLVER = SolverType.CVC4_SOLVER;

		Properties.STOPPING_CONDITION = StoppingCondition.MAXTESTS;
		Properties.SEARCH_BUDGET = 300; // tests
		Properties.MINIMIZATION_TIMEOUT = 60 * 60;
		Properties.ASSERTION_TIMEOUT = 60 * 60;

		Properties.STRATEGY = Strategy.DSE;

		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		Properties.MINIMIZE = true;
		Properties.ASSERTIONS = true;

		assumeTrue(Properties.CVC4_PATH != null);
	}

	@Test
	public void testMazeClientInput() {

		SmartUt smartut = new SmartUt();
		String targetClass = MazeClient.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		assertFalse(best.getTests().isEmpty());

		assertEquals(27, best.getNumOfCoveredGoals());
        assertEquals(0, best.getNumOfNotCoveredGoals());

	}

}
