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
package org.smartut.strategy;

import java.util.ArrayList;
import java.util.List;
import org.smartut.Properties;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientState;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.execution.UncompilableCodeException;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy consists of generating random tests.
 * The property NUM_RANDOM_TESTS is set on the command line
 * 
 * @author gordon
 *
 */
public class FixedNumRandomTestStrategy extends TestGenerationStrategy {

	private final static Logger logger = LoggerFactory.getLogger(FixedNumRandomTestStrategy.class);
	
	@Override
	public TestSuiteChromosome generateTests() {
		LoggingUtils.getSmartUtLogger().info("* Generating fixed number of random tests");
		ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

		RandomLengthTestFactory factory = new org.smartut.testcase.factories.RandomLengthTestFactory();
		TestSuiteChromosome suite = new TestSuiteChromosome();
		if(!canGenerateTestsForSUT()) {
			LoggingUtils.getSmartUtLogger().info("* Found no testable methods in the target class "
					+ Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, 0);
			return suite;
		}

		List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
		List<TestFitnessFunction> goals = new ArrayList<>();
		LoggingUtils.getSmartUtLogger().info("* Total number of test goals: ");
		for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
			goals.addAll(goalFactory.getCoverageGoals());
			LoggingUtils.getSmartUtLogger().info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "")
					+ " " + goalFactory.getCoverageGoals().size());
		}
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

		for (int i = 0; i < Properties.NUM_RANDOM_TESTS; i++) {
			logger.info("Current test: " + i + "/" + Properties.NUM_RANDOM_TESTS);
			TestChromosome test = factory.getChromosome();
			ExecutionResult result = TestCaseExecutor.runTest(test.getTestCase());
			Integer pos = result.getFirstPositionOfThrownException();
			if (pos != null) {
				if (result.getExceptionThrownAtPosition(pos) instanceof CodeUnderTestException
				        || result.getExceptionThrownAtPosition(pos) instanceof UncompilableCodeException
				        || result.getExceptionThrownAtPosition(pos) instanceof TestCaseExecutor.TimeoutExceeded) {
					// Filter invalid tests 
					continue;
				} else {
					// Remove anything that follows an exception
					test.getTestCase().chop(pos + 1);
				}
				test.setChanged(true);
			} else {
				test.setLastExecutionResult(result);
			}
			suite.addTest(test);
		}

		// Evaluate generated suite
		for (TestSuiteFitnessFunction fitnessFunction : getFitnessFunctions()) {
			fitnessFunction.getFitness(suite);
		}

        // Search is finished, send statistics
        sendExecutionStatistics();

		return suite;
	}

}
