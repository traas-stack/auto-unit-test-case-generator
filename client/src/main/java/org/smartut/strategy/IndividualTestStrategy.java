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
package org.smartut.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.ShutdownTestWriter;
import org.smartut.Properties.Criterion;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.rmi.ClientServices;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.ga.stoppingconditions.StoppingCondition;
import org.smartut.rmi.service.ClientState;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.testsuite.TestSuiteMinimizer;
import org.smartut.testsuite.factories.FixedSizeTestSuiteChromosomeFactory;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy selects one coverage goal at a time
 * and generates a test that satisfies it.
 * 
 * The order of goals is randomized. Coincidental coverage is 
 * checked.
 * 
 * @author gordon
 *
 */
public class IndividualTestStrategy extends TestGenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(IndividualTestStrategy.class);
	
	@Override
	public TestSuiteChromosome generateTests() {
		// In order to improve strategy's performance, in here we explicitly disable SmartUt's
		// archive, as it is not used anyway by this strategy
		Properties.TEST_ARCHIVE = false;

		// Set up search algorithm
		LoggingUtils.getSmartUtLogger().info("* Setting up search algorithm for individual test generation");
		ExecutionTracer.enableTraceCalls();

		PropertiesTestGAFactory factory = new PropertiesTestGAFactory();
		
		List<TestSuiteFitnessFunction> fitnessFunctions = getFitnessFunctions();

		long start_time = System.currentTimeMillis() / 1000;

		// Get list of goals
        List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
		// long goalComputationStart = System.currentTimeMillis();
		List<TestFitnessFunction> goals = new ArrayList<>();
		LoggingUtils.getSmartUtLogger().info("* Total number of test goals: ");
        for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
            goals.addAll(goalFactory.getCoverageGoals());
            LoggingUtils.getSmartUtLogger().info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "")
                    + " " + goalFactory.getCoverageGoals().size());
        }

		if(!canGenerateTestsForSUT()) {
			LoggingUtils.getSmartUtLogger().info("* Found no testable methods in the target class "
					+ Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

			return new TestSuiteChromosome();
		}

		// Need to shuffle goals because the order may make a difference
		if (Properties.SHUFFLE_GOALS) {
			// LoggingUtils.getSmartUtLogger().info("* Shuffling goals");
			Randomness.shuffle(goals);
		}
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals,
		                                                                 goals.size());

		LoggingUtils.getSmartUtLogger().info("* Total number of test goals: " + goals.size());

		// Bootstrap with random testing to cover easy goals
		//statistics.searchStarted(suiteGA);
		ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

		StoppingCondition<TestSuiteChromosome> stoppingCondition = getStoppingCondition();
		TestSuiteChromosome suite = bootstrapRandomSuite(fitnessFunctions.get(0), goalFactories.get(0)); // FIXME: just one fitness and one factory?!
		Set<Integer> covered = new HashSet<>();
		int covered_goals = 0;
		int num = 0;

		for (TestFitnessFunction fitness_function : goals) {
			if (fitness_function.isCoveredBy(suite)) {
				covered.add(num);
				covered_goals++;
			}
			num++;
		}
		if (covered_goals > 0)
			LoggingUtils.getSmartUtLogger().info("* Random bootstrapping covered "
			                                         + covered_goals + " test goals");

		int total_goals = goals.size();
		if (covered_goals == total_goals)
			zeroFitness.setFinished();

		int current_budget = 0;

		long total_budget = Properties.SEARCH_BUDGET;
		LoggingUtils.getSmartUtLogger().info("* Budget: "
		                                         + NumberFormat.getIntegerInstance().format(total_budget));

		while (current_budget < total_budget && covered_goals < total_goals
		        && !globalTime.isFinished() && !ShutdownTestWriter.isInterrupted()) {
			long budget = (total_budget - current_budget) / (total_goals - covered_goals);
			logger.info("Budget: " + budget + "/" + (total_budget - current_budget));
			logger.info("Statements: " + current_budget + "/" + total_budget);
			logger.info("Goals covered: " + covered_goals + "/" + total_goals);
			stoppingCondition.setLimit(budget);

			num = 0;
			// int num_statements = 0;
			// //MaxStatementsStoppingCondition.getNumExecutedStatements();
			for (TestFitnessFunction fitnessFunction : goals) {

				if (covered.contains(num)) {
					num++;
					continue;
				}
				
				GeneticAlgorithm<TestChromosome> ga = factory.getSearchAlgorithm();

				// ga.resetStoppingConditions();
				// ga.clearPopulation();
				//ga.setChromosomeFactory(getChromosomeFactory(fitnessFunction));

				if (Properties.PRINT_CURRENT_GOALS)
					LoggingUtils.getSmartUtLogger().info("* Searching for goal " + num + ": "
					                                         + fitnessFunction.toString());
				logger.info("Goal " + num + "/" + (total_goals - covered_goals) + ": "
				        + fitnessFunction);

				if (ShutdownTestWriter.isInterrupted()) {
					num++;
					continue;
				}
				if (globalTime.isFinished()) {
					LoggingUtils.getSmartUtLogger().info("Skipping goal because time is up");
					num++;
					continue;
				}

				// FitnessFunction fitness_function = new
				ga.addFitnessFunction(fitnessFunction);

				// Perform search
				logger.info("Starting evolution for goal " + fitnessFunction);
				ga.generateSolution();

				if (ga.getBestIndividual().getFitness() == 0.0) {
					if (Properties.PRINT_COVERED_GOALS)
						LoggingUtils.getSmartUtLogger().info("* Covered!"); // : " +
					// fitness_function.toString());
					logger.info("Found solution, adding to test suite at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					TestChromosome best = ga.getBestIndividual();
					best.getTestCase().addCoveredGoal(fitnessFunction);
					suite.addTest(best);
					// Calculate and keep track of overall fitness
					for (TestSuiteFitnessFunction fitness_function : fitnessFunctions)
					    fitness_function.getFitness(suite);

					covered_goals++;
					covered.add(num);

					// experiment:
					if (Properties.SKIP_COVERED) {
						Set<Integer> additional_covered_nums = getAdditionallyCoveredGoals(goals,
						                                                                   covered,
						                                                                   best);
						// LoggingUtils.getSmartUtLogger().info("Additionally covered: "+additional_covered_nums.size());
						for (Integer covered_num : additional_covered_nums) {
							covered_goals++;
							covered.add(covered_num);
						}
					}

				} else {
					logger.info("Found no solution for " + fitnessFunction + " at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
				}

				//statistics.iteration(suiteGA);
				if (Properties.REUSE_BUDGET)
					current_budget += stoppingCondition.getCurrentValue();
				else
					current_budget += budget + 1;

				// print console progress bar
				if (Properties.SHOW_PROGRESS
				        && !(Properties.PRINT_COVERED_GOALS || Properties.PRINT_CURRENT_GOALS)) {
					double percent = current_budget;
					percent = percent / total_budget * 100;

					double coverage = covered_goals;
					coverage = coverage / total_goals * 100;

					// ConsoleProgressBar.printProgressBar((int) percent, (int)
					// coverage);
				}

				if (current_budget > total_budget)
					break;
				num++;

				// break;
			}
		}
		if (Properties.SHOW_PROGRESS)
			LoggingUtils.getSmartUtLogger().info("");

		// for testing purposes
		if (globalTime.isFinished())
			LoggingUtils.getSmartUtLogger().info("! Timeout reached");
		if (current_budget >= total_budget)
			LoggingUtils.getSmartUtLogger().info("! Budget exceeded");
		else
			LoggingUtils.getSmartUtLogger().info("* Remaining budget: "
			                                         + (total_budget - current_budget));

//		stoppingCondition.setLimit(Properties.SEARCH_BUDGET);
//		stoppingCondition.forceCurrentValue(current_budget);
//		suiteGA.setStoppingCondition(stopping_condition);
//		suiteGA.addStoppingCondition(global_time);
		// printBudget(suiteGA);

		int c = 0;
		int uncovered_goals = total_goals - covered_goals;
		if (uncovered_goals < 10)
			for (TestFitnessFunction goal : goals) {
				if (!covered.contains(c)) {
					LoggingUtils.getSmartUtLogger().info("! Unable to cover goal " + c + " "
					                                         + goal.toString());
				}
				c++;
			}
		else
			LoggingUtils.getSmartUtLogger().info("! #Goals that were not covered: "
			                                         + uncovered_goals);

		//statistics.searchFinished(suiteGA);
		long end_time = System.currentTimeMillis() / 1000;
		LoggingUtils.getSmartUtLogger().info("* Search finished after "
		                                         + (end_time - start_time)
		                                         + "s, "
		                                         + current_budget
		                                         + " statements, best individual has fitness "
		                                         + suite.getFitness());
        // Search is finished, send statistics
        sendExecutionStatistics();

		LoggingUtils.getSmartUtLogger().info("* Covered " + covered_goals + "/"
		                                         + goals.size() + " goals");
		logger.info("Resulting test suite: " + suite.size() + " tests, length "
		        + suite.totalLengthOfTestCases());


		return suite;
	}

	private Set<Integer> getAdditionallyCoveredGoals(
	        List<? extends TestFitnessFunction> goals, Set<Integer> covered,
	        TestChromosome best) {

		Set<Integer> r = new HashSet<>();
		ExecutionResult result = best.getLastExecutionResult();
		assert (result != null);
		// if (result == null) {
		// result = TestCaseExecutor.getInstance().execute(best.test);
		// }
		int num = -1;
		for (TestFitnessFunction goal : goals) {
			num++;
			if (covered.contains(num))
				continue;
			if (goal.isCovered(best, result)) {
				r.add(num);
				if (Properties.PRINT_COVERED_GOALS)
					LoggingUtils.getSmartUtLogger().info("* Additionally covered: "
					                                         + goal.toString());
			}
		}
		return r;
	}
	
	private TestSuiteChromosome bootstrapRandomSuite(FitnessFunction<?> fitness,
	        TestFitnessFactory<?> goals) {

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
	            || ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)) {
			LoggingUtils.getSmartUtLogger().info("* Disabled random bootstraping for dataflow criterion");
			Properties.RANDOM_TESTS = 0;
		}

		if (Properties.RANDOM_TESTS > 0) {
			LoggingUtils.getSmartUtLogger().info("* Bootstrapping initial random test suite");
		} // else
		  // LoggingUtils.getSmartUtLogger().info("* Bootstrapping initial random test suite disabled!");

		FixedSizeTestSuiteChromosomeFactory factory = new FixedSizeTestSuiteChromosomeFactory(Properties.RANDOM_TESTS);

		TestSuiteChromosome suite = factory.getChromosome();
		if (Properties.RANDOM_TESTS > 0) {
			TestSuiteMinimizer minimizer = new TestSuiteMinimizer(goals);
			minimizer.minimize(suite, true);
			LoggingUtils.getSmartUtLogger().info("* Initial test suite contains "
			                                         + suite.size() + " tests");
		}

		return suite;
	}

}
