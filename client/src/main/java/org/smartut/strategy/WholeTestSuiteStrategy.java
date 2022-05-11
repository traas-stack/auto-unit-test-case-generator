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

import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.result.TestGenerationResultBuilder;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientState;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.testsuite.similarity.DiversityObserver;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;

import java.util.ArrayList;
import java.util.List;

/**
 * Regular whole test suite generation
 * 
 * @author gordon
 *
 */
public class WholeTestSuiteStrategy extends TestGenerationStrategy {

	@Override
	public TestSuiteChromosome generateTests() {
		// Set up search algorithm
		LoggingUtils.getSmartUtLogger().info("* Setting up search algorithm for whole suite generation");
		PropertiesSuiteGAFactory algorithmFactory = new PropertiesSuiteGAFactory();
		GeneticAlgorithm<TestSuiteChromosome> algorithm = algorithmFactory.getSearchAlgorithm();
		
		if(Properties.SERIALIZE_GA || Properties.CLIENT_ON_THREAD)
			TestGenerationResultBuilder.getInstance().setGeneticAlgorithm(algorithm);

		long startTime = System.currentTimeMillis() / 1000;

		// What's the search target
		List<TestSuiteFitnessFunction> fitnessFunctions = getFitnessFunctions();

		algorithm.addFitnessFunctions(fitnessFunctions);
//		for(TestSuiteFitnessFunction f : fitnessFunctions) 
//			algorithm.addFitnessFunction(f);

		// if (Properties.SHOW_PROGRESS && !logger.isInfoEnabled())
		algorithm.addListener(progressMonitor); // FIXME progressMonitor may cause
		// client hang if SmartUt is
		// executed with -prefix!

		if(Properties.TRACK_DIVERSITY)
			algorithm.addListener(new DiversityObserver());

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
				|| ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)
				|| ArrayUtil.contains(Properties.CRITERION, Criterion.STATEMENT)
				|| ArrayUtil.contains(Properties.CRITERION, Criterion.RHO)
				|| ArrayUtil.contains(Properties.CRITERION, Criterion.AMBIGUITY))
			ExecutionTracer.enableTraceCalls();

		// TODO: why it was only if "analyzing"???
		// if (analyzing)
		algorithm.resetStoppingConditions();

		List<TestFitnessFunction> goals = getGoals(true);
		if(!canGenerateTestsForSUT()) {
			LoggingUtils.getSmartUtLogger().info("* Found no testable methods in the target class "
					+ Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

			return new TestSuiteChromosome();
		}

		/*
		 * Proceed with search if CRITERION=EXCEPTION, even if goals is empty
		 */
		TestSuiteChromosome testSuite = null;
		if (!(Properties.STOP_ZERO && goals.isEmpty()) || ArrayUtil.contains(Properties.CRITERION, Criterion.EXCEPTION)) {
			// Perform search
			LoggingUtils.getSmartUtLogger().info("* Using seed {}", Randomness.getSeed() );
			LoggingUtils.getSmartUtLogger().info("* Starting evolution");
			ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

			algorithm.generateSolution();
			// TODO: Refactor MOO!
			// bestSuites = (List<TestSuiteChromosome>) ga.getBestIndividuals();
			testSuite = algorithm.getBestIndividual();
		} else {
			zeroFitness.setFinished();
			testSuite = new TestSuiteChromosome();
			for (FitnessFunction<TestSuiteChromosome> ff : fitnessFunctions) {
				testSuite.setCoverage(ff, 1.0);
			}
		}

		long endTime = System.currentTimeMillis() / 1000;

		goals = getGoals(false); //recalculated now after the search, eg to handle exception fitness
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());
        
		// Newline after progress bar
		if (Properties.SHOW_PROGRESS)
			LoggingUtils.getSmartUtLogger().info("");
		
		if(!Properties.IS_RUNNING_A_SYSTEM_TEST) { //avoid printing time related info in system tests due to lack of determinism
			LoggingUtils.getSmartUtLogger().info("* Search finished after "
					+ (endTime - startTime)
					+ "s and "
					+ algorithm.getAge()
					+ " generations, "
					+ MaxStatementsStoppingCondition.getNumExecutedStatements()
					+ " statements, best individual has fitness: "
					+ testSuite.getFitness());
		}

		// Search is finished, send statistics
		sendExecutionStatistics();

		return testSuite;
	}
	
    private List<TestFitnessFunction> getGoals(boolean verbose) {
        List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
        List<TestFitnessFunction> goals = new ArrayList<>();

        if(goalFactories.size() == 1) {
                TestFitnessFactory<? extends TestFitnessFunction> factory = goalFactories.iterator().next();
                goals.addAll(factory.getCoverageGoals());

                if(verbose) {
                    LoggingUtils.getSmartUtLogger().info("* Total number of test goals: {}", factory.getCoverageGoals().size());
					if (Properties.PRINT_GOALS) {
						for (TestFitnessFunction goal : factory.getCoverageGoals())
							LoggingUtils.getSmartUtLogger().info("" + goal.toString());
					}
				}
        } else {
                if(verbose) {
                        LoggingUtils.getSmartUtLogger().info("* Total number of test goals: ");
                }

                for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
                        goals.addAll(goalFactory.getCoverageGoals());

                        if(verbose) {
                            LoggingUtils.getSmartUtLogger().info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "")
                                                + " " + goalFactory.getCoverageGoals().size());
							if (Properties.PRINT_GOALS) {
								for (TestFitnessFunction goal : goalFactory.getCoverageGoals())
									LoggingUtils.getSmartUtLogger().info("" + goal.toString());
							}
                        }
                }
        }
        return goals;
}
}
