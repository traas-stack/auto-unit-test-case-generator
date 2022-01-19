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

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.TypeSeedingExampleGeneric;
import com.examples.with.different.packagename.TypeSeedingExampleLocale;
import com.examples.with.different.packagename.TypeSeedingExampleString;

public class TypeSeedingSystemTest extends SystemTestBase {
	
	@Test
	public void testStringToObject() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleString.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 5, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testStringToObjectNoSeeding() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleString.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 5, goals);
		Assert.assertEquals("Non-optimal coverage: ", 2d/5d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testLocaleToObject() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleLocale.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 3, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testLocaleToObjectNoSeeding() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleLocale.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 3, goals);
		Assert.assertEquals("Non-optimal coverage: ", 2d/3d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testGenericObject() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleGeneric.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 3, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testGenericObjectNoSeeding() {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeSeedingExampleGeneric.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals("Wrong number of goals: ", 3, goals);
		Assert.assertEquals("Non-optimal coverage: ", 2d/3d, best.getCoverage(), 0.001);
	}
}
