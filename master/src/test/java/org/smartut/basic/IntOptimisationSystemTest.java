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
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.IntExample;

public class IntOptimisationSystemTest extends SystemTestBase {

	private double seedConstants = Properties.PRIMITIVE_POOL;
	
	@After
	public void resetSeedConstants() {
		Properties.PRIMITIVE_POOL = seedConstants;
	}
	
	@Test
	public void testIntSUT() {
		SmartUt smartut = new SmartUt();

		String targetClass = IntExample.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.PRIMITIVE_POOL = 0.0;
		Properties.SEARCH_BUDGET = 100000; // TODO: Can we reduce the variation in results somehow?
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);

        Assert.assertNotNull(result);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
}
