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
package org.smartut.testcase;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.staticfield.StaticFoo;

public class ResetStaticFieldSystemTest extends SystemTestBase {

	private boolean reset_statick_field__property;
	
	@Before
	public void saveProperties() {
		reset_statick_field__property = Properties.RESET_STATIC_FIELDS;
		Properties.RESET_STATIC_FIELDS = true;
		Properties.RESET_STATIC_FIELD_GETS = true;
	}

	@After
	public void restoreProperties() {
		Properties.RESET_STATIC_FIELDS = reset_statick_field__property ;
	}

	@Test
	public void test() {
		SmartUt smartut = new SmartUt();

		String targetClass = StaticFoo.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] {"-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		double best_fitness = best.getFitness();
        Assert.assertEquals("Optimal coverage was not achieved ", 0.0, best_fitness, 0.0);
		
	}

}
