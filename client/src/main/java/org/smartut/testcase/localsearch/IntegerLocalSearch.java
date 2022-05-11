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

package org.smartut.testcase.localsearch;

import org.smartut.ga.localsearch.LocalSearchObjective;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.statements.numeric.NumericalPrimitiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * IntegerLocalSearch class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public class IntegerLocalSearch<T> extends StatementLocalSearch {

	private static final Logger logger = LoggerFactory.getLogger(TestCaseLocalSearch.class);

	private T oldValue;

	/* (non-Javadoc)
	 * @see org.smartut.testcase.LocalSearch#doSearch(org.smartut.testcase.TestChromosome, int, org.smartut.ga.LocalSearchObjective)
	 */
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public boolean doSearch(TestChromosome test, int statement,
	        LocalSearchObjective<TestChromosome> objective) {

		boolean improved = false;
		
		TestCase slice = test.getTestCase().clone();
		int newPos = slice.sliceFor(slice.getStatement(statement).getReturnValue());
		TestCase oldTest = test.getTestCase();
		test.setTestCase(slice);
		test.setChanged(true);
		/**
		 * Commenting the call to getCopyForTest(). It seems unusual that fitness
		 * should be considered on a new test suite instead of the original objective
		 */
//		objective = ((TestSuiteLocalSearchObjective)objective).getCopyForTest(test);
		int oldStatement = statement;
		statement = newPos;

		NumericalPrimitiveStatement<T> p = (NumericalPrimitiveStatement<T>) test.getTestCase().getStatement(statement);
		ExecutionResult oldResult = test.getLastExecutionResult();
		oldValue = p.getValue();
		logger.info("Applying search to: " + p.getCode());

		boolean done = false;
		while (!done) {
			done = true;
			// Try +1
			p.increment(1);
			logger.info("Trying increment of " + p.getCode());
			if (objective.hasImproved(test)) {
				done = false;
				improved = true;

				iterate(2, objective, test, p, statement);
				oldValue = p.getValue();
				oldResult = test.getLastExecutionResult();

			} else {
				// Restore original, try -1
				p.setValue(oldValue);
				test.setLastExecutionResult(oldResult);
				test.setChanged(false);

				p.increment(-1);
				logger.info("Trying decrement of " + p.getCode());
				if (objective.hasImproved(test)) {
					improved = true;
					done = false;
					iterate(-2, objective, test, p, statement);
					oldValue = p.getValue();
					oldResult = test.getLastExecutionResult();

				} else {
					p.setValue(oldValue);
					test.setLastExecutionResult(oldResult);
					test.setChanged(false);
				}
			}
		}

		if(improved) {
			NumericalPrimitiveStatement<T> ps = (NumericalPrimitiveStatement<T>) oldTest.getStatement(oldStatement);
			ps.setValue(p.getValue());
		}
		test.setChanged(true);
		test.setTestCase(oldTest);
		
		logger.info("Finished local search with result " + p.getCode());
		return improved;
	}

	private boolean iterate(long delta, LocalSearchObjective<TestChromosome> objective,
	        TestChromosome test, NumericalPrimitiveStatement<T> p, int statement) {

		boolean improvement = false;
		T oldValue = p.getValue();
		ExecutionResult oldResult = test.getLastExecutionResult();


		p.increment(delta);
		logger.info("Trying increment " + delta + " of " + p.getCode());
		while (objective.hasImproved(test)) {
			oldValue = p.getValue();
			oldResult = test.getLastExecutionResult();
			improvement = true;
			delta = 2 * delta;
			p.increment(delta);
			logger.info("Trying increment " + delta + " of " + p.getCode());
		}
		logger.info("No improvement on " + p.getCode());

		p.setValue(oldValue);
		test.setLastExecutionResult(oldResult);
		test.setChanged(false);
		logger.info("Final value of this iteration: " + p.getValue());

		return improvement;

	}

}
