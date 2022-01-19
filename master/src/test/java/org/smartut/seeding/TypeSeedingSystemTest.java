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
package org.smartut.seeding;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.TestSuiteGenerator;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.seeding.ObjectCastExample;
import com.examples.with.different.packagename.seeding.ObjectInheritanceExample;
import com.examples.with.different.packagename.seeding.TypeExample;

public class TypeSeedingSystemTest extends SystemTestBase {

	@Test
	public void testObjectToStringCase() {
		SmartUt smartut = new SmartUt();

		String targetClass = ObjectCastExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testObjectToStringCaseWithoutSeeding() {
		SmartUt smartut = new SmartUt();

		String targetClass = ObjectCastExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		Assert.assertTrue("Did not expect optimal coverage: ", best.getCoverage() < 1);
	}
	
	@Test
	public void testObjectInheritance() {
		SmartUt smartut = new SmartUt();

		String targetClass = ObjectInheritanceExample.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testObjectInheritanceWithoutSeeding() {
		SmartUt smartut = new SmartUt();

		String targetClass = ObjectInheritanceExample.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		Assert.assertTrue("Did not expect optimal coverage: ", best.getCoverage() < 1);
	}
	
	
	@Test
	public void testTypeExample () {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeExample.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = true;
		Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.BRANCH} ;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = TestSuiteGenerator.getFitnessFactories().get(0).getCoverageGoals().size();
		Assert.assertEquals("Wrong number of goals: ", 4, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testTypeExampleOff () {
		SmartUt smartut = new SmartUt();

		String targetClass = TypeExample.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEED_TYPES = false;


		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);

		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		int goals = TestSuiteGenerator.getFitnessFactories().get(0).getCoverageGoals().size();
		Assert.assertEquals("Wrong number of goals: ", 4, goals);
		Assert.assertTrue("Did not expect optimal coverage: ", best.getCoverage() < 1d);
	}
	
	private String printArray(String[] s) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < s.length ; i++) {
			if (i == 0)
				sb.append(s[i]);
			else
				sb.append(", " + s[i]);
		};
		sb.append("]");
		return sb.toString();
	}
	
}
