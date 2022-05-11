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
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.ArrayLimit;





public class SUTArrayLimitSystemTest extends SystemTestBase {

	public static final int defaultArrayLimit = Properties.ARRAY_LIMIT;

	@After
	public void resetProperties(){
		Properties.ARRAY_LIMIT = defaultArrayLimit;
	}


	@Test
	public void testWithinLimits() {
		SmartUt smartut = new SmartUt();
		String targetClass = ArrayLimit.class.getCanonicalName();
		
		Properties.TARGET_CLASS = targetClass;
		
		String[] command = new String[]{				
				"-generateSuite",
				"-class",
				targetClass
		};
		
		Object result = smartut.parseCommandLine(command);
		
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome)ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n"+best);

		Assert.assertEquals("Non-optimal coverage: ",1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testAboveLimits() {
		SmartUt smartut = new SmartUt();
		String targetClass = ArrayLimit.class.getCanonicalName();
		
		Properties.ARRAY_LIMIT = 10;
		Properties.TARGET_CLASS = targetClass;
		
		String[] command = new String[]{				
				"-generateSuite",
				"-class",
				targetClass
		};
		
		Object result = smartut.parseCommandLine(command);
		
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome)ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n"+best);

		Assert.assertTrue("Optimal coverage: " + best.getCoverage(), best.getCoverage() < 0.99);
	}

}
