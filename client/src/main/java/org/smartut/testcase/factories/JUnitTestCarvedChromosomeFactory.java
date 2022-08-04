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
package org.smartut.testcase.factories;

import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.coverage.FitnessFunctions;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.rmi.ClientServices;
import org.smartut.rmi.service.ClientNodeLocal;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.testcarver.extraction.CarvingManager;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitTestCarvedChromosomeFactory implements
        ChromosomeFactory<TestChromosome> {

	private static final long serialVersionUID = -569338946355072318L;

	private static final Logger logger = LoggerFactory.getLogger(JUnitTestCarvedChromosomeFactory.class);

	private final List<TestCase> junitTests = new ArrayList<>();

	private final ChromosomeFactory<TestChromosome> defaultFactory;

	// These two variables will go once the new statistics frontend is finally finished
	private static int totalNumberOfTestsCarved = 0;

	private static double carvedCoverage = 0.0;

	/**
	 * The carved test cases are used only with a certain probability P. So,
	 * with probability 1-P the 'default' factory is rather used.
	 * 
	 * @param defaultFactory
	 * @throws IllegalStateException
	 *             if Properties are not properly set
	 */
	public JUnitTestCarvedChromosomeFactory(
	        ChromosomeFactory<TestChromosome> defaultFactory)
	        throws IllegalStateException {
		this.defaultFactory = defaultFactory;
		readTestCases();
	}


	private void readTestCases() throws IllegalStateException {
		CarvingManager manager = CarvingManager.getInstance();
		final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
		List<TestCase> tests = manager.getTestsForClass(targetClass);
		junitTests.addAll(tests);

		if (junitTests.size() > 0) {
			totalNumberOfTestsCarved = junitTests.size();

			LoggingUtils.getSmartUtLogger().info("* Using {} carved tests from existing JUnit tests for seeding",
			                                 junitTests.size());
			if (logger.isDebugEnabled()) {
				for (TestCase test : junitTests) {
					logger.debug("Carved Test: {}", test.toCode());
				}
			}

			TestSuiteChromosome suite = new TestSuiteChromosome();
			for (TestCase test : junitTests) {
				suite.addTest(test);
			}

			for (Properties.Criterion pc : Properties.CRITERION) {
				TestSuiteFitnessFunction f = FitnessFunctions.getFitnessFunction(pc);
				f.getFitness(suite);
			}
			carvedCoverage = suite.getCoverage();
		}
		
		ClientNodeLocal client = ClientServices.getInstance().getClientNode();
		client.trackOutputVariable(RuntimeVariable.CarvedTests, totalNumberOfTestsCarved);
		client.trackOutputVariable(RuntimeVariable.CarvedCoverage,carvedCoverage);
	}

	public boolean hasCarvedTestCases() {
		return junitTests.size() > 0;
	}

	public int getNumCarvedTestCases() {
		return junitTests.size();
	}

	public List<TestCase> getCarvedTestCases() {
		return junitTests;
	}

	public TestSuiteChromosome getCarvedTestSuite() {
		TestSuiteChromosome testSuite = new TestSuiteChromosome();
		for (TestCase t : junitTests) {
			testSuite.addTest(t);
		}
		return testSuite;
	}

	@Override
	public TestChromosome getChromosome() {
		final int N_mutations = Properties.SEED_MUTATIONS;
		final double P_clone = Properties.SEED_CLONE;

		double r = Randomness.nextDouble();

		if (r >= P_clone || junitTests.isEmpty()) {
			logger.debug("Using random test");
			return defaultFactory.getChromosome();
		}

		// Cloning
		logger.info("Cloning user test");
		TestCase test = Randomness.choice(junitTests);
		TestChromosome chromosome = new TestChromosome();
		chromosome.setTestCase(test.clone());
		if (N_mutations > 0) {
			int numMutations = Randomness.nextInt(N_mutations);
			logger.debug("Mutations: " + numMutations);

			// Delta
			for (int i = 0; i < numMutations; i++) {
				chromosome.mutate();
			}
		}

		return chromosome;
	}

	public static int getTotalNumberOfTestsCarved() {
		return totalNumberOfTestsCarved;
	}

	public static double getCoverageOfCarvedTests() {
		return carvedCoverage;
	}

}
