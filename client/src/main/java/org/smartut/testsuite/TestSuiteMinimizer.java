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
import org.smartut.TestGenerationContext;
import org.smartut.TimeController;
import org.smartut.Properties.SecondaryObjective;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.coverage.branch.BranchCoverageTestFitness;
import org.smartut.coverage.line.LineCoverageTestFitness;
import org.smartut.coverage.method.MethodCoverageTestFitness;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.junit.CoverageAnalysis;
import org.smartut.junit.writer.TestSuiteWriter;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientState;
import org.smartut.rmi.service.ClientStateInformation;
import org.smartut.setup.TestCluster;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcase.*;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.statements.reflection.PrivateMethodStatement;
import org.smartut.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.utils.generic.GenericAccessibleObject;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

/**
 * <p>
 * TestSuiteMinimizer class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public class TestSuiteMinimizer {

    /**
     * Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(TestSuiteMinimizer.class);

    private final List<TestFitnessFactory<?>> testFitnessFactories = new ArrayList<>();

    /**
     * Assume the search has not started until startTime != 0
     */
    protected static long startTime = 0L;

    /**
     * <p>
     * Constructor for TestSuiteMinimizer.
     * </p>
     *
     * @param factory a {@link org.smartut.coverage.TestFitnessFactory} object.
     */
    public TestSuiteMinimizer(TestFitnessFactory<?> factory) {
        this.testFitnessFactories.add(factory);
    }

    public TestSuiteMinimizer(List<TestFitnessFactory<? extends TestFitnessFunction>> factories) {
        this.testFitnessFactories.addAll(factories);
    }

    /**
     * <p>
     * minimize
     * </p>
     *
     * @param suite             a {@link org.smartut.testsuite.TestSuiteChromosome} object.
     * @param minimizePerTest true is to minimize tests, false is to minimize suites.
     */
    public void minimize(TestSuiteChromosome suite, boolean minimizePerTest) {
        startTime = System.currentTimeMillis();

        SecondaryObjective strategy = Properties.SECONDARY_OBJECTIVE[0];

        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Result_Size,
                suite.size());
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Result_Length,
                suite.totalLengthOfTestCases());

        logger.info("Minimization Strategy: {}, {} tests", strategy, suite.size());
        suite.clearMutationHistory();

        if (minimizePerTest)
            minimizeTests(suite);
        else
            minimizeSuiteByRemoveRedundant(suite);

        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Minimized_Size,
                suite.size());
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Minimized_Length,
                suite.totalLengthOfTestCases());
    }

    private void updateClientStatus(int progress) {
        ClientState state = ClientState.MINIMIZATION;
        ClientStateInformation information = new ClientStateInformation(state);
        information.setProgress(progress);
        ClientServices.getInstance().getClientNode().changeState(state, information);
    }

    private void filterJUnitCoveredGoals(List<TestFitnessFunction> goals) {
        if (Properties.JUNIT.isEmpty())
            return;

        LoggingUtils.getSmartUtLogger().info("* Determining coverage of existing tests");
        String[] testClasses = Properties.JUNIT.split(":");
        for (String testClass : testClasses) {
            try {
                Class<?> junitClass = Class.forName(testClass, true, TestGenerationContext.getInstance().getClassLoaderForSUT());
                Set<TestFitnessFunction> coveredGoals = CoverageAnalysis.getCoveredGoals(junitClass, goals);
                LoggingUtils.getSmartUtLogger().info("* Removing " + coveredGoals.size() + " goals already covered by JUnit (total: " + goals.size() + ")");
                //logger.warn("Removing " + coveredGoals + " goals already covered by JUnit (total: " + goals + ")");
                goals.removeAll(coveredGoals);
                logger.info("Remaining goals: " + goals.size() + ": " + goals);
            } catch(ClassNotFoundException e){
                LoggingUtils.getSmartUtLogger().warn("* Failed to find JUnit test suite: " + Properties.JUNIT);
            }
        }
    }

    /**
     * Minimize test suite with respect to the isCovered Method of the goals
     * defined by the supplied TestFitnessFactory
     *
     * @param suite a {@link org.smartut.testsuite.TestSuiteChromosome} object.
     */
    private void minimizeTests(TestSuiteChromosome suite) {

        logger.info("Minimizing per test");

        ExecutionTracer.enableTraceCalls();

        for (TestChromosome test : suite.getTestChromosomes()) {
            test.setChanged(true); // implies test.clearCachedResults();
        }

        List<TestFitnessFunction> goals = new ArrayList<>();
        for (TestFitnessFactory<?> ff : testFitnessFactories) {
            goals.addAll(ff.getCoverageGoals());
        }
        filterJUnitCoveredGoals(goals);

        int currentGoal = 0;
        int numGoals = goals.size();

        if (Properties.MINIMIZE_SORT)
            Collections.sort(goals);

        Set<TestFitnessFunction> covered = new LinkedHashSet<>();
        List<TestChromosome> minimizedTests = new ArrayList<>();
        TestSuiteWriter minimizedSuite = new TestSuiteWriter();

        for (TestFitnessFunction goal : goals) {
            updateClientStatus(numGoals > 0 ? 100 * currentGoal / numGoals : 100);
            currentGoal++;
            if (isTimeoutReached()) {
				/*
				 * FIXME: if timeout, this algorithm should be changed in a way that the modifications
				 * done so far are not lost
				 */
                logger.warn("Minimization timeout. Roll back to original test suite");
                return;
            }
            logger.info("Considering goal: " + goal);
            if (Properties.MINIMIZE_SKIP_COINCIDENTAL) {
                for (TestChromosome test : minimizedTests) {
                    if (isTimeoutReached()) {
                        logger.warn("Minimization timeout. Roll back to original test suite");
                        return;
                    }
                    if (goal.isCovered(test)) {
                        logger.info("Covered by minimized test: " + goal);
                        covered.add(goal);
                        //test.getTestCase().addCoveredGoal(goal); // FIXME why? goal.isCovered(test) is already adding the goal
                        break;
                    }
                }
            }
            if (covered.contains(goal)) {
                logger.info("Already covered: " + goal);
                logger.info("Now the suite covers " + covered.size() + "/"
                        + goals.size() + " goals");
                continue;
            }

            List<TestChromosome> coveringTests = new ArrayList<>();
            for (TestChromosome test : suite.getTestChromosomes()) {
                if (goal.isCovered(test)) {
                    coveringTests.add(test);
                }
            }
            Collections.sort(coveringTests);
            if (!coveringTests.isEmpty()) {
                TestChromosome test = coveringTests.get(0);
                org.smartut.testcase.TestCaseMinimizer minimizer = new org.smartut.testcase.TestCaseMinimizer(
                        goal);
                TestChromosome copy = test.clone();
                minimizer.minimize(copy);
                if (isTimeoutReached()) {
                    logger.warn("Minimization timeout. Roll back to original test suite");
                    return;
                }
                
                // TODO: Need proper list of covered goals
                copy.getTestCase().clearCoveredGoals();

                // Add ALL goals covered by the minimized test
                for (TestFitnessFunction g : goals) {
                    if (g.isCovered(copy)) { // isCovered(copy) adds the goal
                        covered.add(g);
                        logger.info("Goal covered by minimized test: " + g);
                    }
                }

                minimizedTests.add(copy);
                minimizedSuite.insertTest(copy.getTestCase());

                logger.info("After new test the suite covers " + covered.size() + "/"
                        + goals.size() + " goals");

            } else {
                logger.info("Goal is not covered: " + goal);
            }
        }

        logger.info("Minimized suite covers " + covered.size() + "/" + goals.size()
                + " goals");
        suite.tests.clear();
        for (TestCase test : minimizedSuite.getTestCases()) {
            suite.addTest(test);
        }

        if (Properties.MINIMIZE_SECOND_PASS) {
            removeRedundantTestCases(suite, goals);
        }

        double suiteCoverage = suite.getCoverage();
        logger.info("Setting coverage to: " + suiteCoverage);

        ClientState state = ClientState.MINIMIZATION;
        ClientStateInformation information = new ClientStateInformation(state);
        information.setProgress(100);
        information.setCoverage((int) (Math.round(suiteCoverage * 100)));
        ClientServices.getInstance().getClientNode().changeState(state, information);

        for (TestFitnessFunction goal : goals) {
            if (!covered.contains(goal))
                logger.info("Failed to cover: " + goal);
        }
        // suite.tests = minimizedTests;
    }

    private boolean isTimeoutReached() {
        return !TimeController.getInstance().isThereStillTimeInThisPhase();
    }

    /**
     * Minimize test suite with respect to the isCovered Method of the goals
     * defined by the supplied TestFitnessFactory
     *
     * @param suite a {@link org.smartut.testsuite.TestSuiteChromosome} object.
     */
    private void minimizeSuite(TestSuiteChromosome suite) {

        // Remove previous results as they do not contain method calls
        // in the case of whole suite generation
        for (TestChromosome test : suite.getTestChromosomes()) {
            test.setChanged(true);
            test.clearCachedResults();
        }

        SecondaryObjective strategy = Properties.SECONDARY_OBJECTIVE[0];

        boolean size = false;
        if (strategy == SecondaryObjective.SIZE) {
            size = true;
            // If we want to remove tests, start with shortest
            suite.tests.sort(comparingInt(TestChromosome::size));
        } else if (strategy == SecondaryObjective.MAX_LENGTH) {
            // If we want to remove the longest test, start with longest
            suite.tests.sort((chromosome1, chromosome2) -> chromosome2.size() - chromosome1.size());
        }

        List<TestFitnessFunction> goals = new ArrayList<>();
        List<Double> fitness = new ArrayList<>();
        for (TestFitnessFactory<?> ff : testFitnessFactories) {
            goals.addAll(ff.getCoverageGoals());
            fitness.add(ff.getFitness(suite));
        }

        minimizeByDeleteStatement(suite, size, fitness);

        this.removeEmptyTestCases(suite);
        this.removeRedundantTestCases(suite, goals);
    }

    private void minimizeByDeleteStatement(TestSuiteChromosome suite, boolean size, List<Double> fitness) {
        boolean changed = true;
        while (changed && !isTimeoutReached()) {
            changed = false;

            removeEmptyTestCases(suite);

            for (TestChromosome testChromosome : suite.tests) {
                if (isTimeoutReached())
                    break;
                for (int i = testChromosome.size() - 1; i >= 0; i--) {
                    if (isTimeoutReached())
                        break;

                    logger.debug("Current size: " + suite.size() + "/"
                        + suite.totalLengthOfTestCases());
                    logger.debug("Deleting statement "
                        + testChromosome.getTestCase().getStatement(i).getCode()
                        + " from test");
                    TestChromosome originalTestChromosome = testChromosome.clone();

                    // record exceptions size before delete statement
                    int exceptSizeOriginal = testChromosome.getLastExecutionResult().getAllThrownExceptions().size();

                    boolean modified = false;
                    try {
                        TestFactory testFactory = TestFactory.getInstance();
                        modified = testFactory.deleteStatementGracefully(testChromosome.getTestCase(), i);
                    } catch (ConstructionFailedException e) {
                        modified = false;
                    }

                    if(!modified){
                        testChromosome.setChanged(false);
                        testChromosome.setTestCase(originalTestChromosome.getTestCase());
                        logger.debug("Deleting failed");
                        continue;
                    }

                    testChromosome.setChanged(true);
                    testChromosome.getTestCase().clearCoveredGoals();

                    List<Double> modifiedVerFitness = new ArrayList<>();
                    for (TestFitnessFactory<?> ff : testFitnessFactories)
                        modifiedVerFitness.add(ff.getFitness(suite));

                    int compare_ff = 0;
                    // record exceptions size after delete statement
                    int exceptSizeDeleted = testChromosome.getLastExecutionResult().getAllThrownExceptions().size();
                    boolean exceptionCheckPass = true;
                    if(exceptSizeDeleted > exceptSizeOriginal) {
                        logger.debug("exceptSizeOriginal is {}, exceptSizeDeleted is {}", exceptSizeOriginal, exceptSizeDeleted);
                        exceptionCheckPass = false;
                        compare_ff = 1;
                    }
                    // check exception first, then check fitness
                    if(exceptionCheckPass) {
                        for (int i_fit = 0; i_fit < modifiedVerFitness.size(); i_fit++) {
                            if (Double.compare(modifiedVerFitness.get(i_fit), fitness.get(i_fit)) < 0) {
                                compare_ff = -1; // new value is lower than previous one
                                break;
                            } else if (Double.compare(modifiedVerFitness.get(i_fit), fitness.get(i_fit)) > 0) {
                                compare_ff = 1; // new value is greater than previous one
                                break;
                            }
                        }
                    }

                    // the value 0 if d1 (previous fitness) is numerically equal to d2 (new fitness)
                    if (compare_ff == 0) {
                        continue; // if we can guarantee that we have the same fitness value with less statements, better
                        // a value less than 0 if d1 is numerically less than d2
                    } else if (compare_ff == -1) {
                        fitness = modifiedVerFitness;
                        changed = true;
                        /**
                         * This means, that we try to delete statements equally
                         * from each test case (If size is 'false'.) The hope is
                         * that the median length of the test cases is shorter,
                         * as opposed to the average length.
                         */
                        if (!size)
                            break;
                    }
                    // and a value greater than 0 if d1 is numerically greater than d2
                    else if (compare_ff == 1) {
                        // Restore previous state
                        logger.debug("Can't remove statement "
                            + originalTestChromosome.getTestCase().getStatement(i).getCode());
                        logger.debug("Restoring fitness from " + modifiedVerFitness
                            + " to " + fitness);
                        testChromosome.setTestCase(originalTestChromosome.getTestCase());
                        testChromosome.setLastExecutionResult(originalTestChromosome.getLastExecutionResult());
                        testChromosome.setChanged(false);
                    }
                }
            }
        }
    }

    /**
     * Minimize test suite by remove redundantTests
     * defined by the supplied TestFitnessFactory
     *
     * @param suite a {@link org.smartut.testsuite.TestSuiteChromosome} object.
     */
    public void minimizeSuiteByRemoveRedundant(TestSuiteChromosome suite) {

        // Remove previous results as they do not contain method calls
        // in the case of whole suite generation
        // because re-calculate coverage after last mock change, no longer needed here
        //for (TestChromosome test : suite.getTestChromosomes()) {
        //    test.setChanged(true);
        //    test.clearCachedResults();
        //}

        SecondaryObjective strategy = Properties.SECONDARY_OBJECTIVE[0];

        if (strategy == SecondaryObjective.SIZE) {
            // If we want to remove tests, start with shortest
            suite.tests.sort(comparingInt(TestChromosome::size));
        } else if (strategy == SecondaryObjective.MAX_LENGTH) {
            // If we want to remove the longest test, start with longest
            suite.tests.sort((chromosome1, chromosome2) -> chromosome2.size() - chromosome1.size());
        }

        List<TestFitnessFunction> goals = new ArrayList<>();
        for (TestFitnessFactory<?> ff : testFitnessFactories) {
            goals.addAll(ff.getCoverageGoals());
        }

        removeEmptyTestCases(suite);

        if(suite.size() == 1) {
            return;
        }
        this.removeRedundantTestCasesWithRerun(suite, goals);
    }

    /**
     * gracefully delete statement per test in suite
     * @param suite   Test suite
     */
    public void minimizeByDeleteStatementPerTest(TestSuiteChromosome suite) {
        SecondaryObjective strategy = Properties.SECONDARY_OBJECTIVE[0];

        boolean size = false;
        if (strategy == SecondaryObjective.SIZE) {
            size = true;
            // If we want to remove tests, start with shortest
            suite.tests.sort(comparingInt(TestChromosome::size));
        } else if (strategy == SecondaryObjective.MAX_LENGTH) {
            // If we want to remove the longest test, start with longest
            suite.tests.sort((chromosome1, chromosome2) -> chromosome2.size() - chromosome1.size());
        }

        // delete statement per test
        List<TestChromosome> testChromosomes = suite.tests;
        TestSuiteChromosome copy = suite.clone();
        for(TestChromosome chromosome : testChromosomes) {
            copy.tests.clear();
            copy.tests.add(chromosome);

            List<Double> fitness = new ArrayList<>();
            for (TestFitnessFactory<?> ff : testFitnessFactories) {
                fitness.add(ff.getFitness(copy));
            }
            minimizeByDeleteStatement(copy, size, fitness);
        }

        removeEmptyTestCases(suite);
    }

    public static void removeEmptyTestCases(TestSuiteChromosome suite) {
        Iterator<TestChromosome> it = suite.tests.iterator();
        while (it.hasNext()) {
            TestChromosome test = it.next();
            if (test.size() == 0) {
                logger.debug("Removing empty test case");
                it.remove();
            }
        }
    }

    private void removeRedundantTestCases(TestSuiteChromosome suite, List<TestFitnessFunction> goals) {
        // Subsuming tests are inserted in the back, so we start inserting the final tests from there
        List<TestChromosome> tests = suite.getTestChromosomes();
        logger.debug("Before removing redundant tests: {}", tests.size());

        if(Properties.MINIMIZATION_GOALS_FILTER) {
            sortTestChromosomeByCoveredGoalsDesc(tests);
        } else {
            Collections.reverse(tests);
        }
        List<TestChromosome> finalTests = new ArrayList<>();
        Set<TestFitnessFunction> coveredGoals = new LinkedHashSet<>();

        for (TestChromosome test : tests) {
            boolean addsNewGoals = false;
            for (TestFitnessFunction goal : goals) {
                if (!coveredGoals.contains(goal)) {
                    if (goal.isCovered(test)) {
                        addsNewGoals = true;
                        coveredGoals.add(goal);
                    }
                }
            }

            if (addsNewGoals) {
                coveredGoals.addAll(test.getTestCase().getCoveredGoals());
                finalTests.add(test);
            }
        }
        if(!Properties.MINIMIZATION_GOALS_FILTER) {
            Collections.reverse(finalTests);
        }
        suite.getTestChromosomes().clear();
        suite.getTestChromosomes().addAll(finalTests);
        logger.debug("After removing redundant tests: {}", tests.size());

    }

    /**
     * sort test chromosome by covered goals descend
     * TestChromosome is sorted by fitnessValues original
     * @param tests list to sort
     */
    private void sortTestChromosomeByCoveredGoalsDesc(List<TestChromosome> tests) {
        tests.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            long coveredFilterGoals1 = 0L;
            for (TestFitnessFunction fitnessFunction : o1.getTestCase().getCoveredGoals()) {
                if (((fitnessFunction instanceof LineCoverageTestFitness))) {
                    // || (fitnessFunction instanceof BranchCoverageTestFitness))) {
                    coveredFilterGoals1++;
                }
            }

            long coveredFilterGoals2 = 0L;
            for (TestFitnessFunction testFitnessFunction : o2.getTestCase().getCoveredGoals()) {
                if (((testFitnessFunction instanceof LineCoverageTestFitness))) {
                    //|| testFitnessFunction instanceof BranchCoverageTestFitness))) {
                    coveredFilterGoals2++;
                }
            }

            int i = (int)Math.signum(coveredFilterGoals2 - coveredFilterGoals1);

            // check exception number first, then check length
            if (i == 0) {
                // less exception rank higher
                int o1ExceptsSize = 0;
                int o2ExceptSize = 0;
                if(o1.getLastExecutionResult() != null) {
                    o1ExceptsSize = o1.getLastExecutionResult().getAllThrownExceptions().size();
                }
                if(o2.getLastExecutionResult() != null) {
                    o2ExceptSize = o2.getLastExecutionResult().getAllThrownExceptions().size();
                }
                int exceptCmpRet = (int)Math.signum(o1ExceptsSize - o2ExceptSize);

                // for Total_length, the shorter, the better
                if(exceptCmpRet == 0) {
                    return o1.compareSecondaryObjective(o2);
                }

                return exceptCmpRet;
            }
            return i;
        });
    }

    private void removeRedundantTestCasesWithRerun(TestSuiteChromosome suite, List<TestFitnessFunction> goals) {
        // first remove case without mut
        removeCaseNotHasMut(suite);

        // Subsuming tests are inserted in the back, so we start inserting the final tests from there
        List<TestChromosome> tests = suite.getTestChromosomes();
        logger.warn("Before removing redundant tests with rerun: {}", tests.size());

        // because re-calculate coverage after last mock change, no longer needed here
        //for (TestChromosome test : tests) {
        //    test.setChanged(true);
        //    for (TestFitnessFunction goal : goals) {
        //        goal.isCovered(test);
        //    }
        //}

        if(Properties.MINIMIZATION_GOALS_FILTER) {
            // filter goals by line & branch coverage
            goals = goals.stream()
                .filter(goal -> goal instanceof LineCoverageTestFitness || goal instanceof BranchCoverageTestFitness)
                .collect(Collectors.toList());
            sortTestChromosomeByCoveredGoalsDesc(tests);
        } else {
            Collections.reverse(tests);
        }
        List<TestChromosome> finalTests = new ArrayList<>();
        Set<TestFitnessFunction> coveredGoals = new LinkedHashSet<>();

        for (TestChromosome test : tests) {
            boolean addsNewGoals = false;
            for (TestFitnessFunction goal : goals) {
                if (!coveredGoals.contains(goal)) {
                    if (goal.isCovered(test)) {
                        // some goals should consider the exception to the run result
                        if(!calcCoveredGoalsIgnoreRunResult(goal)) {
                            ExecutionResult executionResult = test.getLastExecutionResult();
                            if(executionResult != null) {
                                if(executionResult.getAllThrownExceptions().size() > 0){
                                    continue;
                                }
                            }
                        }
                        addsNewGoals = true;
                        break;
                    }
                }
            }

            if (addsNewGoals) {
                coveredGoals.addAll(test.getTestCase().getCoveredGoals());
                finalTests.add(test);
            }
        }

        if(!Properties.MINIMIZATION_GOALS_FILTER) {
            Collections.reverse(finalTests);
        }
        suite.getTestChromosomes().clear();
        suite.getTestChromosomes().addAll(finalTests);
        logger.warn("After removing redundant tests: {}", tests.size());
    }

    private boolean calcCoveredGoalsIgnoreRunResult(TestFitnessFunction goal) {
        return goal instanceof LineCoverageTestFitness || goal instanceof BranchCoverageTestFitness
            || goal instanceof MethodCoverageTestFitness;
    }

    /**
     * remove case which doesn't have method under test
     * @param suite
     */
    public static void removeCaseNotHasMut(TestSuiteChromosome suite) {
        List<TestChromosome> tests = suite.getTestChromosomes();
        List<TestChromosome> filterRet = new ArrayList<>();

        // get all methods under test
        List<GenericAccessibleObject<?>> allTestMethods = TestCluster.getInstance().getOriginalTestCalls();
        List<GenericAccessibleObject<?>> testMethodFilterCons = TestCluster.getInstance().filterConstructors(allTestMethods);
        // cache method name
        Set<String> methodNameSet = testMethodFilterCons.stream().map(GenericAccessibleObject::getName).collect(
            Collectors.toSet());

        if(methodNameSet.size() == 0) {
            return;
        }

        for (TestChromosome test : tests) {
            // first check whether has method of class under test
            boolean containsMethod = false;
            for(Statement stmt : test.getTestCase()) {
                // containing privateMethodStatement means has the mut
                if(stmt instanceof PrivateMethodStatement) {
                    containsMethod = true;
                    break;
                }
                if(stmt instanceof MethodStatement) {
                    if(methodNameSet.contains(((MethodStatement)stmt).getMethodName())) {
                        containsMethod = true;
                        break;
                    }
                }
            }

            if(containsMethod) {
                filterRet.add(test);
            }
        }

        suite.clearTests();
        suite.addTests(filterRet);
    }
}
