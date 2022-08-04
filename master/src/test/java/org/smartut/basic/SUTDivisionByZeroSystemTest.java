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
package org.smartut.basic;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.DivisionByZero;

public class SUTDivisionByZeroSystemTest extends SystemTestBase {

	/*
	 * To avoid side effects on test cases that we will run afterwards,
	 * if we modify some values in Properties, then we need to re-int them after
	 * each test case execution
	 */
	public static final double defaultPrimitivePool = Properties.PRIMITIVE_POOL;
	public static final boolean defaultErrorBranches = Properties.ERROR_BRANCHES;

	@After
	public void resetProperties() {
		Properties.PRIMITIVE_POOL = defaultPrimitivePool;
		Properties.ERROR_BRANCHES = defaultErrorBranches;
	}

	@Test
	public void testDivisionByZero() {
		SmartUt smartut = new SmartUt();

		String targetClass = DivisionByZero.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.PRIMITIVE_POOL = 0.99;
		Properties.ERROR_BRANCHES = true;
		Properties.CRITERION = new Properties.Criterion[] {Properties.Criterion.BRANCH, Properties.Criterion.TRYCATCH};

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		/*
		 * 1: default constructor
		 * 1: method testMe
		 * 2: extra branch for division by 0
		 * 2: for underflow
		 */
		Assert.assertEquals(2, TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size());
		Assert.assertEquals(4, TestGenerationStrategy.getFitnessFactories().get(1).getCoverageGoals().size());

		double coverage = best.getCoverage();
		//one of the underflow branches is difficult to get without DSE/LS
		Assert.assertTrue("Not good enough coverage: "+coverage, coverage > 0.83d);
	}
}
