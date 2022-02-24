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
package org.smartut.ga.metaheuristics.mosa;

import org.smartut.Properties;
import org.smartut.Properties.SelectionFunction;
import org.smartut.coverage.FitnessFunctions;
import org.smartut.coverage.exception.ExceptionCoverageSuiteFitness;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.archive.Archive;
import org.smartut.ga.comparators.DominanceComparator;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.setup.TestCluster;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.secondaryobjectives.TestCaseSecondaryObjective;
import org.smartut.testcase.statements.*;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.utils.ArrayUtil;
import org.smartut.utils.BudgetConsumptionMonitor;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.utils.generic.GenericAccessibleObject;

import java.util.*;

/**
 * Abstract class for MOSA or variants of MOSA.
 *
 * @author Annibale Panichella, Fitsum M. Kifetew
 */
public abstract class AbstractMOSA extends GeneticAlgorithm<TestChromosome> {

	private static final long serialVersionUID = 146182080947267628L;

	private static final Logger logger = LoggerFactory.getLogger(AbstractMOSA.class);

	// Explicitly declared with a more special type than the one used in GeneticAlgorithm.
	// This is required for the Archive, which currently only supports TestFitnessFunctions.
	protected final List<TestFitnessFunction> fitnessFunctions = new ArrayList<>();

	private MOSATestSuiteAdapter adapter = null;

	/** Keep track of overall suite fitness functions and correspondent test fitness functions */
	public final Map<TestSuiteFitnessFunction, Class<?>> suiteFitnessFunctions;

	/** Object used to keep track of the execution time needed to reach the maximum coverage */
	protected final BudgetConsumptionMonitor budgetMonitor;

	/**
	 * Constructor.
	 *
	 * @param factory a {@link org.smartut.ga.ChromosomeFactory} object
	 */
	public AbstractMOSA(ChromosomeFactory<TestChromosome> factory) {
		super(factory);

		this.suiteFitnessFunctions = new LinkedHashMap<>();
		for (Properties.Criterion criterion : Properties.CRITERION) {
			TestSuiteFitnessFunction suiteFit = FitnessFunctions.getFitnessFunction(criterion);
			Class<?> testFit = FitnessFunctions.getTestFitnessFunctionClass(criterion);
			this.suiteFitnessFunctions.put(suiteFit, testFit);
		}

		this.budgetMonitor = new BudgetConsumptionMonitor();

		// set the secondary objectives of test cases (useful when MOSA compares two test
		// cases to, for example, update the archive)
		TestCaseSecondaryObjective.setSecondaryObjectives();

		if (Properties.SELECTION_FUNCTION != SelectionFunction.RANK_CROWD_DISTANCE_TOURNAMENT) {
			LoggingUtils.getSmartUtLogger()
					.warn("Originally, MOSA was implemented with a '"
							+ SelectionFunction.RANK_CROWD_DISTANCE_TOURNAMENT.name()
							+ "' selection function. You may want to consider using it.");
		}
	}

	public void setAdapter(final MOSATestSuiteAdapter adapter) {
		Objects.requireNonNull(adapter);
		if (this.adapter == null) {
			this.adapter = adapter;
		} else {
			throw new IllegalStateException("adapter has already been set");
		}
	}

	@Override
	public void addFitnessFunction(final FitnessFunction<TestChromosome> function) {
		if (function instanceof TestFitnessFunction) {
			fitnessFunctions.add((TestFitnessFunction) function);
		} else {
			throw new IllegalArgumentException("Only TestFitnessFunctions are supported");
		}
	}

	@Override
	public FitnessFunction<TestChromosome> getFitnessFunction() {
		return fitnessFunctions.get(0);
	}

	@Override
	public List<? extends FitnessFunction<TestChromosome>> getFitnessFunctions() {
		return fitnessFunctions;
	}

	/**
	 * This method is used to generate new individuals (offspring) from
	 * the current population. The offspring population has the same size as the parent population.
	 *
	 * @return offspring population
	 */
	protected List<TestChromosome> breedNextGeneration() {
		List<TestChromosome> offspringPopulation = new ArrayList<>(Properties.POPULATION);
		// we apply only Properties.POPULATION/2 iterations since in each generation
		// we generate two offsprings
		for (int i = 0; i < Properties.POPULATION / 2 && !this.isFinished(); i++) {
			// select best individuals

			/*
			 * the same individual could be selected twice! Is this a problem for crossover?
			 * Because crossing over an individual with itself will most certainly give you the
			 * same individual again...
			 */

			TestChromosome parent1 = this.selectionFunction.select(this.population);
			TestChromosome parent2 = this.selectionFunction.select(this.population);
			TestChromosome offspring1 = parent1.clone();
			TestChromosome offspring2 = parent2.clone();
			// no more crossover
			// apply crossover
			//if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE) {
			//	try {
			//		this.crossoverFunction.crossOver(offspring1, offspring2);
			//	} catch (ConstructionFailedException e) {
			//		logger.debug("CrossOver failed.");
			//		continue;
			//	}
			//}

			this.removeUnusedVariables(offspring1);
			this.removeUnusedVariables(offspring2);

			// apply mutation on offspring1
			this.mutate(offspring1, parent1);
			if (offspring1.isChanged()) {
				this.clearCachedResults(offspring1);
				offspring1.updateAge(this.currentIteration);
				this.calculateFitness(offspring1);
				offspringPopulation.add(offspring1);
			}

			// apply mutation on offspring2
			this.mutate(offspring2, parent2);
			if (offspring2.isChanged()) {
				this.clearCachedResults(offspring2);
				offspring2.updateAge(this.currentIteration);
				this.calculateFitness(offspring2);
				offspringPopulation.add(offspring2);
			}
		}
		// Not generate new test during evolve
		// Add new randomly generate tests
//		for (int i = 0; i < Properties.POPULATION * Properties.P_TEST_INSERTION; i++) {
//			final TestChromosome tch;
//			if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()) {
//				tch = this.chromosomeFactory.getChromosome();
//				tch.setChanged(true);
//			} else {
//				tch = Randomness.choice(this.getSolutions()).clone();
//				tch.mutate();
////				tch.mutate(); // TODO why is it mutated twice?
//			}
//			if (tch.isChanged()) {
//				tch.updateAge(this.currentIteration);
//				this.calculateFitness(tch);
//				offspringPopulation.add(tch);
//			}
//		}
		logger.info("Number of offsprings = {}", offspringPopulation.size());
		return offspringPopulation;
	}

	/**
	 * Method used to mutate an offspring.
	 *
	 * @param offspring the offspring chromosome
	 * @param parent the parent chromosome that {@code offspring} was created from
	 */
	private void mutate(TestChromosome offspring, TestChromosome parent) {
		offspring.mutate();
		if (!offspring.isChanged()) {
			// if offspring is not changed, we try to mutate it once again
			offspring.mutate();
		}
		if (!this.hasMethodCall(offspring)) {
			offspring.setTestCase(parent.getTestCase().clone());
			boolean changed = offspring.mutationInsert();
			if (changed) {
				offspring.getTestCase().forEach(Statement::isValid);
			}
			offspring.setChanged(changed);
		}
		this.notifyMutation(offspring);
	}

	/**
	 * This method checks whether the test has only primitive type statements. Indeed,
	 * crossover and mutation can lead to tests with no method calls (methods or constructors
	 * call), thus, when executed they will never cover something in the class under test.
	 *
	 * @param test to check
	 * @return true if the test has at least one method or constructor call (i.e., the test may
	 * cover something when executed; false otherwise
	 */
	private boolean hasMethodCall(TestChromosome test) {
		boolean flag = false;
		TestCase tc = test.getTestCase();
		for (Statement s : tc) {
			if (s instanceof MethodStatement) {
				MethodStatement ms = (MethodStatement) s;
				boolean isTargetMethod = ms.getDeclaringClassName().equals(Properties.TARGET_CLASS);
				if (isTargetMethod) {
					return true;
				}
			}
			if (s instanceof ConstructorStatement) {
				ConstructorStatement ms = (ConstructorStatement) s;
				boolean isTargetMethod = ms.getDeclaringClassName().equals(Properties.TARGET_CLASS);
				if (isTargetMethod) {
					return true;
				}
			}
		}
		return flag;
	}

	/**
	 * This method clears the cached results for a specific chromosome (e.g., fitness function
	 * values computed in previous generations). Since a test case is changed via crossover
	 * and/or mutation, previous data must be recomputed.
	 *
	 * @param chromosome TestChromosome to clean
	 */
	private void clearCachedResults(TestChromosome chromosome) {
		chromosome.clearCachedMutationResults();
		chromosome.clearCachedResults();
		chromosome.clearMutationHistory();
		chromosome.getFitnessValues().clear();
	}

	/**
	 * When a test case is changed via crossover and/or mutation, it can contains some
	 * primitive variables that are not used as input (or to store the output) of method calls.
	 * Thus, this method removes all these "trash" statements.
	 *
	 * @param chromosome
	 * @return true or false depending on whether "unused variables" are removed
	 */
	private boolean removeUnusedVariables(TestChromosome chromosome) {
		final int sizeBefore = chromosome.size();
		final TestCase t = chromosome.getTestCase();
		final List<Integer> toDelete = new ArrayList<>(chromosome.size());
		boolean hasDeleted = false;

		int num = 0;
		for (Statement s : t) {
			final VariableReference var = s.getReturnValue();
			final boolean delete = s instanceof PrimitiveStatement || s instanceof ArrayStatement;
			if (!t.hasReferences(var) && delete) {
				toDelete.add(num);
				hasDeleted = true;
			}
			num++;
		}
		toDelete.sort(Collections.reverseOrder());
		for (int position : toDelete) {
			t.remove(position);
		}
		final int sizeAfter = chromosome.size();
		if (hasDeleted) {
			logger.debug("Removed {} unused statements", (sizeBefore - sizeAfter));
		}
		return hasDeleted;
	}

	/**
	 * This method extracts non-dominated solutions (tests) according to all covered goal
	 * (e.g., branches).
	 *
	 * @param solutions list of test cases to analyze with the "dominance" relationship
	 * @return the non-dominated set of test cases
	 */
	public List<TestChromosome> getNonDominatedSolutions(List<TestChromosome> solutions) {
		final DominanceComparator<TestChromosome> comparator =
				new DominanceComparator<>(this.getCoveredGoals());
		final List<TestChromosome> nextFront = new ArrayList<>(solutions.size());
		boolean isDominated;
		for (TestChromosome p : solutions) {
			isDominated = false;
			List<TestChromosome> dominatedSolutions = new ArrayList<>(solutions.size());
			for (TestChromosome best : nextFront) {
				final int flag = comparator.compare(p, best);
				if (flag < 0) {
					dominatedSolutions.add(best);
				}
				if (flag > 0) {
					isDominated = true;
				}
			}
			if (isDominated) {
				continue;
			}

			nextFront.add(p);
			nextFront.removeAll(dominatedSolutions);
		}
		return nextFront;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializePopulation() {
		logger.info("executing initializePopulation function");

		this.notifySearchStarted();
		this.currentIteration = 0;

		// init population size
		initPopulationSize();
		// Create a random parent population P0
		this.generateInitialPopulation(Properties.POPULATION);

		// Determine fitness
		this.calculateFitness();
		this.notifyIteration();
	}

	private void initPopulationSize() {
		// population size related to method size
		List<GenericAccessibleObject<?>> allTestMethods = TestCluster.getInstance().getOriginalTestCalls();
		// need to filter constructors, public method only
		List<GenericAccessibleObject<?>> testMethodFilterCons = TestCluster.getInstance().filterConstructors(allTestMethods);
		// need to add private Method
		int privateMethodsSize = TestFactory.getInstance().getPrivateMethodsSize();
		int totalMethodSize = privateMethodsSize + testMethodFilterCons.size();
		logger.warn("Before init, method to test size is {}", totalMethodSize);
		int initPopulation = totalMethodSize * 2 + 1 < Properties.POPULATION ? Properties.POPULATION
			: totalMethodSize * 2 + 1;
		logger.warn("Before init, population size is {}", initPopulation);
		try {
			Properties.getInstance().setValue("population", initPopulation);
		} catch (Exception e) {
			throw new Error("Invalid value for population: " + e.getMessage());
		}
	}

	/**
	 * Returns the goals that have been covered by the test cases stored in the archive.
	 *
	 * @return
	 */
	protected Set<TestFitnessFunction> getCoveredGoals() {
		return new LinkedHashSet<>(Archive.getArchiveInstance().getCoveredTargets());
	}

	/**
	 * Returns the number of goals that have been covered by the test cases stored in the archive.
	 *
	 * @return
	 */
	protected int getNumberOfCoveredGoals() {
		return Archive.getArchiveInstance().getNumberOfCoveredTargets();
	}

	protected void addUncoveredGoal(TestFitnessFunction goal) {
		Archive.getArchiveInstance().addTarget(goal);
	}

	/**
	 * Returns the goals that have not been covered by the test cases stored in the archive.
	 *
	 * @return
	 */
	protected Set<TestFitnessFunction> getUncoveredGoals() {
		return new LinkedHashSet<>(Archive.getArchiveInstance().getUncoveredTargets());
	}

	/**
	 * Returns the goals that have not been covered by the test cases stored in the archive.
	 *
	 * @return
	 */
	protected int getNumberOfUncoveredGoals() {
		return Archive.getArchiveInstance().getNumberOfUncoveredTargets();
	}

	/**
	 * Returns the total number of goals, i.e., number of covered goals + number of uncovered goals.
	 *
	 * @return
	 */
	protected int getTotalNumberOfGoals() {
		return Archive.getArchiveInstance().getNumberOfTargets();
	}

	/**
	 * Return the test cases in the archive as a list.
	 *
	 * @return
	 */
	protected List<TestChromosome> getSolutions() {
		return new ArrayList<>(Archive.getArchiveInstance().getSolutions());
	}

	/**
	 * Generates a {@link org.smartut.testsuite.TestSuiteChromosome} object with all test cases
	 * in the archive.
	 *
	 * @return
	 */
	public TestSuiteChromosome generateSuite() {
		TestSuiteChromosome suite = new TestSuiteChromosome();
		Archive.getArchiveInstance().getSolutions().forEach(suite::addTest);
		return suite;
	}

	///// ----------------------

	/**
	 * Some methods of the super class (i.e., {@link org.smartut.ga.metaheuristics.GeneticAlgorithm}
	 * class) require a {@link org.smartut.testsuite.TestSuiteChromosome} object. However, MOSA
	 * evolves {@link TestChromosome} objects. Therefore, we must override
	 * those methods and create a {@link org.smartut.testsuite.TestSuiteChromosome} object with all
	 * the evolved {@link TestChromosome} objects (either in the population or
	 * in the {@link org.smartut.ga.archive.Archive}.
	 */

	// This override should no longer be needed since MOSA no longer accepts ProgressMonitors
//	/**
//     * Notify all search listeners but ProgressMonitor of fitness evaluation.
//     *
//     * @param chromosome a {@link org.smartut.ga.Chromosome} object.
//     */
//    @Override
//	protected void notifyEvaluation(TestChromosome chromosome) {
//		// ProgressMonitor requires a TestSuiteChromosome
//		Stream<SearchListener<TestChromosome>> ls = listeners.stream().filter(l -> !(l instanceof ProgressMonitor));
//		ls.forEach(l -> l.fitnessEvaluation(chromosome));
//	}

	// This override should no longer be needed since MOSA no longer accepts ProgressMonitors
//    /**
//     * Notify all search listeners but ProgressMonitor of a mutation.
//     *
//     * @param chromosome a {@link org.smartut.ga.Chromosome} object.
//     */
//    @Override
//    protected void notifyMutation(TestChromosome chromosome) {
//		// ProgressMonitor requires a TestSuiteChromosome
//		Stream<SearchListener<TestChromosome>> ls = listeners.stream().filter(l -> !(l instanceof ProgressMonitor));
//		ls.forEach(l -> l.modification(chromosome));
//    }

	@Override
	protected void notifySearchStarted() {
		super.notifySearchStarted();
	}

	@Override
	protected void notifyIteration() {
		super.notifyIteration();
	}

	@Override
	protected void notifySearchFinished() {
		super.notifySearchFinished();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void calculateFitness(TestChromosome c) {
		this.fitnessFunctions.forEach(fitnessFunction -> fitnessFunction.getFitness(c));

		// if one of the coverage criterion is Criterion.EXCEPTION, then we have to analyse the results
		// of the execution to look for generated exceptions
		if (ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.EXCEPTION)) {
			ExceptionCoverageSuiteFitness.calculateExceptionInfo(
					Collections.singletonList(c.getLastExecutionResult()),
					new HashMap<>(), new HashMap<>(), new HashMap<>(), new ExceptionCoverageSuiteFitness());
		}

		this.notifyEvaluation(c);
		// update the time needed to reach the max coverage
		this.budgetMonitor.checkMaxCoverage(this.getNumberOfCoveredGoals());
	}

	/**
	 * {@inheritDoc}
	 */
//    @SuppressWarnings("unchecked")
//    @Override
//    public List<TestSuiteChromosome> getBestIndividuals() {
//        // get final test suite (i.e., non dominated solutions in Archive)
//        TestSuiteChromosome bestTestCases = Archive.getArchiveInstance().mergeArchiveAndSolution(new TestSuiteChromosome());
//        if (bestTestCases.getTestChromosomes().isEmpty()) {
//          for (TestChromosome test : this.getNonDominatedSolutions(this.population)) {
//            bestTestCases.addTest(test);
//          }
//        }
//
//        // compute overall fitness and coverage
//        this.computeCoverageAndFitness(bestTestCases);
//
//		return Collections.singletonList(bestTestCases);
//    }

	@Override
	public List<TestChromosome> getBestIndividuals() {
		return this.getNonDominatedSolutions(this.population);
	}

//	/**
//     * {@inheritDoc}
//     *
//     * <p>This method is used by the Progress Monitor at the and of each generation to show the total coverage reached by the algorithm.
//     * Since the Progress Monitor requires a {@link org.smartut.testsuite.TestSuiteChromosome} object, this method artificially creates
//     * a {@link org.smartut.testsuite.TestSuiteChromosome} object as the union of all solutions stored in the {@link
//     * org.smartut.ga.archive.Archive}.</p>
//     *
//     * <p>The coverage score of the {@link org.smartut.testsuite.TestSuiteChromosome} object is given by the percentage of targets marked
//     * as covered in the archive.</p>
//     *
//     * @return a {@link org.smartut.testsuite.TestSuiteChromosome} object to be consumable by the Progress Monitor.
//     */
//    @Override
//    public TestSuiteChromosome getBestIndividual() {
//        TestSuiteChromosome best = this.generateSuite();
//        if (best.getTestChromosomes().isEmpty()) {
//          for (TestChromosome test : this.getNonDominatedSolutions(this.population)) {
//            best.addTest(test);
//          }
//          for (TestSuiteFitnessFunction suiteFitness : this.suiteFitnessFunctions.keySet()) {
//            best.setCoverage(suiteFitness, 0.0);
//            best.setFitness(suiteFitness,  1.0);
//          }
//          return best;
//        }
//
//        // compute overall fitness and coverage
//        this.computeCoverageAndFitness(best);
//
//        return best;
//    }

//    protected void computeCoverageAndFitness(TestSuiteChromosome suite) {
//      for (Entry<TestSuiteFitnessFunction, Class<?>> entry : this.suiteFitnessFunctions
//          .entrySet()) {
//        TestSuiteFitnessFunction suiteFitnessFunction = entry.getKey();
//        Class<?> testFitnessFunction = entry.getValue();
//
//        int numberCoveredTargets =
//            Archive.getArchiveInstance().getNumberOfCoveredTargets(testFitnessFunction);
//        int numberUncoveredTargets =
//            Archive.getArchiveInstance().getNumberOfUncoveredTargets(testFitnessFunction);
//        int totalNumberTargets = numberCoveredTargets + numberUncoveredTargets;
//
//        double coverage = totalNumberTargets == 0 ? 1.0
//            : ((double) numberCoveredTargets) / ((double) totalNumberTargets);
//
//        suite.setFitness(suiteFitnessFunction, numberUncoveredTargets);
//        suite.setCoverage(suiteFitnessFunction, coverage);
//        suite.setNumOfCoveredGoals(suiteFitnessFunction, numberCoveredTargets);
//        suite.setNumOfNotCoveredGoals(suiteFitnessFunction, numberUncoveredTargets);
//      }
//    }

	protected void applyLocalSearch(final TestSuiteChromosome testSuite) {
		adapter.applyLocalSearch(testSuite);
	}
}
