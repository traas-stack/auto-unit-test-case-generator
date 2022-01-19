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
import org.smartut.SystemTestBase;
import org.smartut.Properties.Criterion;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.concolic.FileSuffix;

/**
 * Created by Andrea Arcuri on 19/03/15.
 */
public class FileSuffixLSSystemTest extends SystemTestBase {

	@Before
	public void init() {
		Properties.LOCAL_SEARCH_PROBABILITY = 1.0;
		Properties.LOCAL_SEARCH_RATE = 1;
		Properties.LOCAL_SEARCH_BUDGET_TYPE = Properties.LocalSearchBudgetType.TESTS;
		Properties.LOCAL_SEARCH_BUDGET = 100;
		Properties.SEARCH_BUDGET = 10;
		Properties.STOPPING_CONDITION = Properties.StoppingCondition.MAXTIME;
		Properties.RESET_STATIC_FIELD_GETS = true;

	}

	@Test
	public void testLocalSearch() {

		SmartUt smartut = new SmartUt();
		String targetClass = FileSuffix.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		Properties.PRINT_TO_SYSTEM = true;
		Properties.DSE_PROBABILITY = 0.0; // force using only LS, no DSE
		Properties.CRITERION = new Criterion[] {
	            //these are basic criteria that should be always on by default
	            Criterion.LINE, Criterion.BRANCH, Criterion.EXCEPTION, Criterion.WEAKMUTATION, Criterion.OUTPUT, Criterion.METHOD, Criterion.METHODNOEXCEPTION, Criterion.CBRANCH  };

		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);


	}

}
