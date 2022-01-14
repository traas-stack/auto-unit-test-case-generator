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
package org.smartut.coverage;

import java.util.List;

import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.SmartUtError;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Abstract TestCoverageGoal class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public abstract class TestCoverageGoal {

	/** Constant <code>logger</code> */
	protected final static Logger logger = LoggerFactory.getLogger(TestCoverageGoal.class);


	/**
	 * Return true if this coverage goal is covered by the given test
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 * @return a boolean.
	 */
	public abstract boolean isCovered(TestChromosome test);

	/**
	 * Determine if there is an existing test case covering this goal
	 * 
	 * @param tests
	 *            a {@link java.util.List} object.
	 * @return a boolean.
	 */
	public boolean isCovered(List<TestChromosome> tests) {
		for (TestChromosome test : tests) {
			if (isCovered(test))
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * hasTimeout
	 * </p>
	 * 
	 * @param result
	 *            a {@link org.smartut.testcase.execution.ExecutionResult} object.
	 * @return a boolean.
	 */
	public static boolean hasTimeout(ExecutionResult result) {

		if (result == null) {
			logger.warn("Result is null!");
			return false;
		} else if (result.test == null) {
			logger.warn("Test is null!");
			return false;
		}
		int size = result.test.size();
		if (result.isThereAnExceptionAtPosition(size)) {
			if (result.getExceptionThrownAtPosition(size) instanceof TestCaseExecutor.TimeoutExceeded) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Execute a test case
	 * 
	 * @param test
	 *            The test case to execute
	 * @return Result of the execution
	 */
	protected ExecutionResult runTest(TestChromosome test) {

		if (!test.isChanged() && test.getLastExecutionResult() != null)
			return test.getLastExecutionResult();

		try {
			ExecutionResult result = TestCaseExecutor.getInstance().execute(test.getTestCase());
			return result;
		} catch (Exception e) {
			logger.error("TG: Exception caught: ", e);
			throw new SmartUtError(e);
		}
	}
}
