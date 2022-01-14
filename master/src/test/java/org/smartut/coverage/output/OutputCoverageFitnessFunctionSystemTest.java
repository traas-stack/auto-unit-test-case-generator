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
package org.smartut.coverage.output;

import com.examples.with.different.packagename.coverage.*;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.SystemTestBase;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.*;

/**
 * @author Jose Miguel Rojas
 *
 */
public class OutputCoverageFitnessFunctionSystemTest extends SystemTestBase {

    private static final Criterion[] defaultCriterion = Properties.CRITERION;
    
    private static boolean defaultArchive = Properties.TEST_ARCHIVE;

	@After
	public void resetProperties() {
		Properties.CRITERION = defaultCriterion;
		Properties.TEST_ARCHIVE = defaultArchive;
	}

	@Before
	public void beforeTest() {
        Properties.CRITERION = new Properties.Criterion[] { Criterion.BRANCH, Criterion.OUTPUT };
	}

	@Test
	public void testOutputCoveragePrimitiveTypesWithArchive() {
		Properties.TEST_ARCHIVE = true;
		testOutputCoveragePrimitiveTypes();
	}
	
	@Test
	public void testOutputCoveragePrimitiveTypesWithoutArchive() {
		Properties.TEST_ARCHIVE = false;
		testOutputCoveragePrimitiveTypes();
	}
		
	public void testOutputCoveragePrimitiveTypes() {
		SmartUt smartut = new SmartUt();
		
		String targetClass = MethodReturnsPrimitive.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = 0;
		for (TestFitnessFactory ff : TestGenerationStrategy.getFitnessFactories())
			goals += ff.getCoverageGoals().size();
		Assert.assertEquals("Unexpected number of goals", 24, goals);
		Assert.assertEquals("Non-optimal fitness: ", 0.0, best.getFitness(), 0.001);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testOutputCoverageWrapperTypes() {
		SmartUt smartut = new SmartUt();

		String targetClass = MethodReturnsWrapper.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		int goals = 0;
		for (TestFitnessFactory ff : TestGenerationStrategy.getFitnessFactories())
			goals += ff.getCoverageGoals().size();
		Assert.assertEquals("Unexpected number of goals", 29, goals);
		Assert.assertEquals("Non-optimal fitness: ", 5.0, best.getFitness(), 0.001);
	}

	@Test
	public void testOutputCoverageObjectType() {
		SmartUt smartut = new SmartUt();

		String targetClass = MethodReturnsObject.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		//Properties.SEARCH_BUDGET = 60;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		int goals = 0;
		for (TestFitnessFactory ff : TestGenerationStrategy.getFitnessFactories())
			goals += ff.getCoverageGoals().size();
		Assert.assertEquals("Unexpected number of goals", 11, goals);
		Assert.assertEquals("Unexpected coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testOutputCoverageArray() {
		SmartUt smartut = new SmartUt();

		String targetClass = MethodReturnsArray.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		int goals = 0;
		for (TestFitnessFactory ff : TestGenerationStrategy.getFitnessFactories())
			goals += ff.getCoverageGoals().size();
		Assert.assertEquals("Unexpected number of goals", 15, goals);
		Assert.assertEquals("Non-optimal fitness: ", 0.0, best.getFitness(), 0.001);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testOutputCoverageIgnoreHashCode() {
		SmartUt smartut = new SmartUt();

		String targetClass = ClassWithHashCode.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		int goals = 0;
		for (TestFitnessFactory ff : TestGenerationStrategy.getFitnessFactories())
			goals += ff.getCoverageGoals().size();
		Assert.assertEquals("Unexpected number of goals", 2, goals);
		Assert.assertEquals("Non-optimal fitness: ", 0.0, best.getFitness(), 0.001);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
}
