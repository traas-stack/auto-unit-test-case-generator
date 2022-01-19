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

import java.util.List;

import org.smartut.ga.FitnessFunction;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testsuite.TestSuiteChromosome;

/**
 * Abstract base class for fitness functions for test case chromosomes
 *
 * @author Gordon Fraser
 */
public abstract class TestFitnessFunction
		extends FitnessFunction<TestChromosome>
		implements Comparable<TestFitnessFunction> {

	private static final long serialVersionUID = 5602125855207061901L;

	static boolean warnedAboutIsSimilarTo = false;

	/**
	 * <p>
	 * getFitness
	 * </p>
	 *
	 * @param individual
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 * @param result
	 *            a {@link org.smartut.testcase.execution.ExecutionResult} object.
	 * @return a double.
	 */
	public abstract double getFitness(TestChromosome individual, ExecutionResult result);

	/** {@inheritDoc} */
	@Override
	public double getFitness(TestChromosome individual) {
		logger.trace("Executing test case on original");
		ExecutionResult origResult = individual.getLastExecutionResult();
		if (origResult == null || individual.isChanged()) {
			origResult = runTest(individual.test);
			individual.setLastExecutionResult(origResult);
			individual.setChanged(false);
		}

		double fitness = getFitness(individual, origResult);
		updateIndividual(individual, fitness);

		return fitness;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Used to preorder goals by difficulty
	 */
	@Override
	public abstract int compareTo(TestFitnessFunction other);

	protected final int compareClassName(TestFitnessFunction other){
		return this.getClass().getName().compareTo(other.getClass().getName());
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object other);

	/** {@inheritDoc} */
	public ExecutionResult runTest(TestCase test) {
		return TestCaseExecutor.runTest(test);
	}

	/**
	 * Determine if there is an existing test case covering this goal
	 *
	 * @param tests
	 *            a {@link java.util.List} object.
	 * @return a boolean.
	 */
	public boolean isCovered(List<TestCase> tests) {
		return tests.stream().anyMatch(this::isCovered);
	}

	/**
	 * Determine if there is an existing test case covering this goal
	 *
	 * @param tests
	 *            a {@link java.util.List} object.
	 * @return a boolean.
	 */
	public boolean isCoveredByResults(List<ExecutionResult> tests) {
		return tests.stream().anyMatch(this::isCovered);
	}

	public boolean isCoveredBy(TestSuiteChromosome testSuite) {
		int num = 1;
		for (TestChromosome test : testSuite.getTestChromosomes()) {
			logger.debug("Checking goal against test "+num+"/"+testSuite.size());
			num++;
			if (isCovered(test))
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * isCovered
	 * </p>
	 *
	 * @param test
	 *            a {@link org.smartut.testcase.TestCase} object.
	 * @return a boolean.
	 */
	public boolean isCovered(TestCase test) {
		TestChromosome c = new TestChromosome();
		c.test = test;
		return isCovered(c);
	}

	/**
	 * <p>
	 * isCovered
	 * </p>
	 *
	 * @param tc
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 * @return a boolean.
	 */
	public boolean isCovered(TestChromosome tc) {
		if(tc.getTestCase().isGoalCovered(this)){
			return true;
		}

		ExecutionResult result = tc.getLastExecutionResult();
		if (result == null || tc.isChanged()) {
			result = runTest(tc.test);
			tc.setLastExecutionResult(result);
			tc.setChanged(false);
		}

		return isCovered(tc, result);
	}

	/**
	 * <p>
	 * isCovered
	 * </p>
	 *
	 * @param individual
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 * @param result
	 *            a {@link org.smartut.testcase.execution.ExecutionResult} object.
	 * @return a boolean.
	 */
	public boolean isCovered(TestChromosome individual, ExecutionResult result) {
		boolean covered = getFitness(individual, result) == 0.0;
		if (covered) {
			individual.test.addCoveredGoal(this);
		}
		return covered;
	}

	/**
	 * Helper function if this is used without a chromosome
	 *
	 * @param result
	 * @return
	 */
	public boolean isCovered(ExecutionResult result) {
		TestChromosome chromosome = new TestChromosome();
		chromosome.setTestCase(result.test);
		chromosome.setLastExecutionResult(result);
		chromosome.setChanged(false);
		return isCovered(chromosome, result);
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.FitnessFunction#isMaximizationFunction()
	 */
	/** {@inheritDoc} */
	@Override
	public boolean isMaximizationFunction() {
		return false;
	}

	public abstract String getTargetClass();

	public abstract String getTargetMethod();
}
