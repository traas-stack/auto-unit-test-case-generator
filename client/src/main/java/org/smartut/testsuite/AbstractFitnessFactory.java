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
package org.smartut.testsuite;

import org.smartut.Properties;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionTracer;

/**
 * Historical concrete TestFitnessFactories only implement the getGoals() method
 * of TestFitnessFactory. Those old Factories can just extend these
 * AstractFitnessFactory to support the new method getFitness()
 * 
 * @author Sebastian Steenbuck
 */
public abstract class AbstractFitnessFactory<T extends TestFitnessFunction> implements
        TestFitnessFactory<T> {

	/**
	 * A concrete factory can store the time consumed to initially compute all
	 * coverage goals in this field in order to track this information in
	 * SearchStatistics.
	 */
	public static long goalComputationTime = 0L;

	
	protected boolean isCUT(String className) {
		return Properties.TARGET_CLASS.equals("")
				|| (className.equals(Properties.TARGET_CLASS)
				|| className.startsWith(Properties.TARGET_CLASS + "$"));
	}
	
	/** {@inheritDoc} */
	@Override
	public double getFitness(TestSuiteChromosome suite) {

		ExecutionTracer.enableTraceCalls();

		int coveredGoals = 0;
		for (T goal : getCoverageGoals()) {
			for (TestChromosome test : suite.getTestChromosomes()) {
				if (goal.isCovered(test)) {
					coveredGoals++;
					break;
				}
			}
		}

		ExecutionTracer.disableTraceCalls();

		return getCoverageGoals().size() - coveredGoals;
	}
}
