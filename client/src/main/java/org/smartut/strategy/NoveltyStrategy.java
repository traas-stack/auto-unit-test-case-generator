package org.smartut.strategy;

import org.smartut.Properties;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.archive.Archive;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.NoveltySearch;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.novelty.BranchNoveltyFunction;
import org.smartut.novelty.SuiteFitnessEvaluationListener;
import org.smartut.result.TestGenerationResultBuilder;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientState;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NoveltyStrategy extends TestGenerationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(NoveltyStrategy.class);


    @Override
    public TestSuiteChromosome generateTests() {
        // Set up search algorithm
        LoggingUtils.getSmartUtLogger().info("* Setting up search algorithm for novelty search");

        PropertiesNoveltySearchFactory algorithmFactory = new PropertiesNoveltySearchFactory();
        NoveltySearch algorithm = algorithmFactory.getSearchAlgorithm();
        //NoveltySearch<TestChromosome, TestSuiteChromosome> algorithm = new NoveltySearch<TestChromosome, TestSuiteChromosome>(chromosomeFactory);

        if(Properties.SERIALIZE_GA || Properties.CLIENT_ON_THREAD)
            TestGenerationResultBuilder.getInstance().setGeneticAlgorithm(algorithm);

        long startTime = System.currentTimeMillis() / 1000;

        // What's the search target
        List<TestSuiteFitnessFunction> fitnessFunctions = getFitnessFunctions();

        SuiteFitnessEvaluationListener listener = new SuiteFitnessEvaluationListener(fitnessFunctions);
        algorithm.addListener(listener);
        algorithm.setNoveltyFunction(new BranchNoveltyFunction());

        // if (Properties.SHOW_PROGRESS && !logger.isInfoEnabled())
        //algorithm.addListener(progressMonitor); // FIXME progressMonitor expects testsuitechromosomes

        if(Properties.TRACK_DIVERSITY) {
            // The DiversityObserver is only implemented for test suites, so we can't use it here
            // algorithm.addListener(new DiversityObserver());
            throw new RuntimeException("Tracking population diversity is not supported by novelty search");
        }
        // ===========================================================================================

        if (ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.DEFUSE)
                || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.ALLDEFS)
                || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.STATEMENT)
                || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.RHO)
                || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.AMBIGUITY))
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
        if (!(Properties.STOP_ZERO && goals.isEmpty()) || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.EXCEPTION)) {
            // Perform search
            LoggingUtils.getSmartUtLogger().info("* Using seed {}", Randomness.getSeed() );
            LoggingUtils.getSmartUtLogger().info("* Starting evolution");
            ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

            algorithm.generateSolution();
            testSuite = Archive.getArchiveInstance().mergeArchiveAndSolution(new TestSuiteChromosome());
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
