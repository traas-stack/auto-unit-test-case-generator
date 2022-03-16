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
package org.smartut.localsearch;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.TestGenerationContext;
import org.smartut.coverage.branch.BranchCoverageSuiteFitness;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.ga.localsearch.DefaultLocalSearchObjective;
import org.smartut.ga.localsearch.LocalSearchObjective;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.testdata.SmartUtFile;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.localsearch.BranchCoverageMap;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.StringPrimitiveStatement;
import org.smartut.testcase.statements.environment.FileNamePrimitiveStatement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.localsearch.TestSuiteLocalSearch;
import org.smartut.utils.generic.GenericClass;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericMethod;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.localsearch.DseBar;
import com.examples.with.different.packagename.localsearch.DseFoo;

public class EnvironmentDataSystemTest extends SystemTestBase {

	@Before
	public void init() {
		Properties.LOCAL_SEARCH_PROBABILITY = 1.0;
		Properties.LOCAL_SEARCH_RATE = 1;
		Properties.LOCAL_SEARCH_BUDGET_TYPE = Properties.LocalSearchBudgetType.TESTS;
		Properties.LOCAL_SEARCH_BUDGET = 100;
		Properties.SEARCH_BUDGET = 50000;
		RuntimeSettings.useVFS = true;
		Properties.RESET_STATIC_FIELD_GETS = true;

	}

	@Test
	public void testOnSpecificTest() throws ClassNotFoundException, ConstructionFailedException, NoSuchMethodException, SecurityException {
		Properties.TARGET_CLASS = DseBar.class.getCanonicalName();
		Class<?> sut = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
		Class<?> fooClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(DseFoo.class.getCanonicalName());
		GenericClass clazz = new GenericClass(sut);

		DefaultTestCase test = new DefaultTestCase();

		// String string0 = "baz5";
		VariableReference stringVar = test.addStatement(new StringPrimitiveStatement(test, "baz5"));

		// DseFoo dseFoo0 = new DseFoo();
		GenericConstructor fooConstructor = new GenericConstructor(fooClass.getConstructors()[0], fooClass);
		ConstructorStatement fooConstructorStatement = new ConstructorStatement(test, fooConstructor, Arrays.asList(new VariableReference[] {}));
		VariableReference fooVar = test.addStatement(fooConstructorStatement);

		// String fileName = new String("/home/galeotti/README.txt")
		String path = "/home/galeotti/README.txt";
		SmartUtFile smartutFile = new SmartUtFile(path);
		FileNamePrimitiveStatement fileNameStmt = new FileNamePrimitiveStatement(test,smartutFile);
		test.addStatement(fileNameStmt);
		
		Method fooIncMethod = fooClass.getMethod("inc", new Class<?>[] { });
		GenericMethod incMethod = new GenericMethod(fooIncMethod, fooClass);
		test.addStatement(new MethodStatement(test, incMethod, fooVar, Arrays.asList(new VariableReference[] {})));
		test.addStatement(new MethodStatement(test, incMethod, fooVar, Arrays.asList(new VariableReference[] {})));
		test.addStatement(new MethodStatement(test, incMethod, fooVar, Arrays.asList(new VariableReference[] {})));
		test.addStatement(new MethodStatement(test, incMethod, fooVar, Arrays.asList(new VariableReference[] {})));
		test.addStatement(new MethodStatement(test, incMethod, fooVar, Arrays.asList(new VariableReference[] {})));

		// DseBar dseBar0 = new DseBar(string0);
		GenericConstructor gc = new GenericConstructor(clazz.getRawClass().getConstructors()[0], clazz);
		ConstructorStatement constructorStatement = new ConstructorStatement(test, gc, Arrays.asList(new VariableReference[] {stringVar}));
		VariableReference callee = test.addStatement(constructorStatement);

		// dseBar0.coverMe(dseFoo0);
		Method m = clazz.getRawClass().getMethod("coverMe", new Class<?>[] { fooClass});
		GenericMethod method = new GenericMethod(m, sut);
		MethodStatement ms = new MethodStatement(test, method, callee, Arrays.asList(new VariableReference[] {fooVar}));
		test.addStatement(ms);
		System.out.println(test);
		
		TestSuiteChromosome suite = new TestSuiteChromosome();
		BranchCoverageSuiteFitness fitness = new BranchCoverageSuiteFitness();

		BranchCoverageMap.getInstance().searchStarted(null);
		assertEquals(4.0, fitness.getFitness(suite), 0.1F);
		suite.addTest(test);
		assertEquals(1.0, fitness.getFitness(suite), 0.1F);

		System.out.println("Test suite: "+suite);
		
		Properties.CONCOLIC_TIMEOUT = Integer.MAX_VALUE;
		
		TestSuiteLocalSearch localSearch = TestSuiteLocalSearch.selectTestSuiteLocalSearch();
		LocalSearchObjective<TestSuiteChromosome> localObjective = new DefaultLocalSearchObjective();
		localObjective.addFitnessFunction(fitness);
		localSearch.doSearch(suite, localObjective);
		System.out.println("Fitness: "+fitness.getFitness(suite));
		System.out.println("Test suite: "+suite);
		assertEquals("Local search failed to cover class", 0.0, fitness.getFitness(suite), 0.1F);
		BranchCoverageMap.getInstance().searchFinished(null);
	}


}
