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

package org.smartut.instrumentation.testability;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.IntegerCollectionAllBranch;
import com.examples.with.different.packagename.IntegerContainerBranch;

/**
 * @author Gordon Fraser
 * 
 */
public class ContainerTransformationSystemTest extends SystemTestBase {

	@Test
	public void testIntegerList() {
		SmartUt smartut = new SmartUt();

		String targetClass = IntegerContainerBranch.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEARCH_BUDGET = 80000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

        Assert.assertNotNull(result);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 5, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testIntegerListList() {
		SmartUt smartut = new SmartUt();

		String targetClass = IntegerCollectionAllBranch.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEARCH_BUDGET = 180000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

        Assert.assertNotNull(result);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 5, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
}
