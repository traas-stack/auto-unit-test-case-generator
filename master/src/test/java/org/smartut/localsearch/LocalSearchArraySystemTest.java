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
package org.smartut.localsearch;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.smartut.Properties;
import org.smartut.Properties.LocalSearchBudgetType;
import org.smartut.SystemTestBase;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.branch.BranchCoverageSuiteFitness;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.ga.localsearch.DefaultLocalSearchObjective;
import org.smartut.ga.localsearch.LocalSearchObjective;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.localsearch.BranchCoverageMap;
import org.smartut.testcase.statements.ArrayStatement;
import org.smartut.testcase.statements.AssignmentStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.numeric.IntPrimitiveStatement;
import org.smartut.testcase.variable.ArrayIndex;
import org.smartut.testcase.variable.ArrayReference;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.localsearch.TestSuiteLocalSearch;
import org.smartut.utils.generic.GenericClass;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericMethod;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.localsearch.ArrayLengthExample;
import com.examples.with.different.packagename.localsearch.BasicArrayExample;

public class LocalSearchArraySystemTest extends SystemTestBase {

	@Before
    public void init(){
        Properties.DSE_PROBABILITY = 0.0;
        Properties.PRIMITIVE_POOL = 0.0;
		Properties.LOCAL_SEARCH_BUDGET_TYPE = LocalSearchBudgetType.TESTS;
		Properties.LOCAL_SEARCH_BUDGET = 1000;
		Properties.LOCAL_SEARCH_REFERENCES = false;
		Properties.LOCAL_SEARCH_ARRAYS = true;
		Properties.RESET_STATIC_FIELD_GETS = true;

    }
	
	
	private TestCase getArrayTest(int length) throws NoSuchMethodException, SecurityException, ConstructionFailedException, ClassNotFoundException {
		Class<?> sut = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
		GenericClass clazz = new GenericClass(sut);
		
		DefaultTestCase test = new DefaultTestCase();
		GenericConstructor gc = new GenericConstructor(clazz.getRawClass().getConstructors()[0], clazz);

		TestFactory testFactory = TestFactory.getInstance();
		VariableReference callee = testFactory.addConstructor(test, gc, 0, 0);
		VariableReference arrayVar = test.addStatement(new ArrayStatement(test, int[].class, length));

		for(int i = 0; i < length; i++) {
			// Add value
			VariableReference intVar = test.addStatement(new IntPrimitiveStatement(test, 0));
			test.addStatement(new AssignmentStatement(test, new ArrayIndex(test, (ArrayReference) arrayVar, i), intVar));

		}
		
		Method m = clazz.getRawClass().getMethod("testMe", new Class<?>[] { int[].class });
		GenericMethod method = new GenericMethod(m, sut);
		MethodStatement ms = new MethodStatement(test, method, callee, Arrays.asList(new VariableReference[] {arrayVar}));
		test.addStatement(ms);

		return test;
	}
	
	private void runArrayExample(int length, double expectedFitness) throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		TestCase test = getArrayTest(length);
		System.out.println("Test: "+test.toCode());

		TestSuiteChromosome suite = new TestSuiteChromosome();
		BranchCoverageSuiteFitness fitness = new BranchCoverageSuiteFitness();

		BranchCoverageMap.getInstance().searchStarted(null);
		assertEquals(4.0, fitness.getFitness(suite), 0.1F);
		suite.addTest(test);
		//assertEquals(1.0, fitness.getFitness(suite), 0.1F);
		
		TestSuiteLocalSearch localSearch = TestSuiteLocalSearch.selectTestSuiteLocalSearch();
		LocalSearchObjective<TestSuiteChromosome> localObjective = new DefaultLocalSearchObjective();
		localObjective.addFitnessFunction(fitness);
		localSearch.doSearch(suite, localObjective);
		System.out.println("Fitness: "+fitness.getFitness(suite));
		System.out.println("Test suite: "+suite);
		assertEquals(expectedFitness, fitness.getFitness(suite), 0.1F);
		BranchCoverageMap.getInstance().searchFinished(null);
	}
	
	@Test
	public void testEmptyArrayLengthLocalSearch() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = ArrayLengthExample.class.getCanonicalName();
		runArrayExample(0, 0.0);
	}

	@Test
	public void testArrayLengthLocalSearch() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = ArrayLengthExample.class.getCanonicalName();
		runArrayExample(2, 0.0);
	}

	@Test
	public void testLongArrayLengthLocalSearch() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = ArrayLengthExample.class.getCanonicalName();
		runArrayExample(10, 0.0);
	}
	
	@Test
	public void testBasicArrayLocalSearch() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = BasicArrayExample.class.getCanonicalName();
		runArrayExample(4, 0.0);
	}

	@Test
	public void testBasicArrayLocalSearchAndLength() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = BasicArrayExample.class.getCanonicalName();
		runArrayExample(0, 1.0); // Requires double execution
	}

}
