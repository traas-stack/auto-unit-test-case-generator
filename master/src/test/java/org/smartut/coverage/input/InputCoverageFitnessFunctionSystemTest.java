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
package org.smartut.coverage.input;

import com.examples.with.different.packagename.coverage.ClassWithField;
import com.examples.with.different.packagename.coverage.MethodWithPrimitiveInputArguments;
import com.examples.with.different.packagename.coverage.MethodWithSeveralInputArguments;
import com.examples.with.different.packagename.coverage.MethodWithWrapperInputArguments;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.SystemTestBase;
import org.smartut.coverage.FitnessFunctions;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.statements.AssignmentStatement;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.numeric.BooleanPrimitiveStatement;
import org.smartut.testcase.variable.FieldReference;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericField;
import org.smartut.utils.generic.GenericMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


/**
 * @author Jose Miguel Rojas
 *
 */
public class InputCoverageFitnessFunctionSystemTest extends SystemTestBase {

    private static final Criterion[] defaultCriterion = Properties.CRITERION;
    
    private static boolean defaultArchive = Properties.TEST_ARCHIVE;

	@After
	public void resetProperties() {
		Properties.CRITERION = defaultCriterion;
		Properties.TEST_ARCHIVE = defaultArchive;
	}

	@Before
	public void beforeTest() {
		Properties.CRITERION = new Properties.Criterion[] {Criterion.INPUT};
	}

	@Test
	public void testInputCoverage() {
		SmartUt smartut = new SmartUt();
		
		String targetClass = MethodWithSeveralInputArguments.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.MAX_ARRAY = 2;
		Properties.NULL_PROBABILITY = 0.2;
		Properties.SEARCH_BUDGET = 20000;
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		List<?> goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals();
		Assert.assertEquals(12, goals.size());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testInputCoverageWithPrimitiveTypes() {
		SmartUt smartut = new SmartUt();

		String targetClass = MethodWithPrimitiveInputArguments.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		List<?> goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals();
		Assert.assertEquals(23, goals.size());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}


	@Test
	public void testInputCoverageWithWrapperTypes() {
		SmartUt smartut = new SmartUt();

		String targetClass = MethodWithWrapperInputArguments.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		List<?> goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals();
		Assert.assertEquals(31, goals.size());
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}

	@Test
	public void testInputCoverageClassWithField() throws NoSuchFieldException, NoSuchMethodException {
		Class<?> sut = ClassWithField.class;

		DefaultTestCase tc = new DefaultTestCase();
		// ClassWithField classWithField0 = new ClassWithField();
		GenericConstructor constructor = new GenericConstructor(sut.getConstructors()[0], sut);
		ConstructorStatement constructorStatement = new ConstructorStatement(tc, constructor, Arrays.asList(new VariableReference[] {}));
		VariableReference obj = tc.addStatement(constructorStatement);

		// classWithField0.testFoo(classWithField0.BOOLEAN_FIELD);
		FieldReference field = new FieldReference(tc, new GenericField(sut.getDeclaredField("BOOLEAN_FIELD"), sut),obj);
		Method m = sut.getMethod("testFoo", new Class<?>[] { Boolean.TYPE});
		GenericMethod gm = new GenericMethod(m, sut);
		tc.addStatement(new MethodStatement(tc, gm, obj, Arrays.asList(new VariableReference[] {field})));

		// classWithField0.BOOLEAN_FIELD = false;
		VariableReference boolRef = tc.addStatement(new BooleanPrimitiveStatement(tc,false));
		tc.addStatement(new AssignmentStatement(tc, field, boolRef));
		tc.addStatement(new MethodStatement(tc, gm, obj, Arrays.asList(new VariableReference[] {field})));

		Properties.TARGET_CLASS = sut.getCanonicalName();
		Properties.JUNIT_TESTS = true;

		TestSuiteChromosome testSuite = new TestSuiteChromosome();
		testSuite.addTest(tc);

		FitnessFunction ffunction = FitnessFunctions.getFitnessFunction(Properties.Criterion.INPUT);
		assertEquals("Should be 0.0", 0.0, ffunction.getFitness(testSuite), 0.0);
		assertEquals("Should be 1.0", 1.0, testSuite.getCoverage(ffunction), 0.0);

	}

}
