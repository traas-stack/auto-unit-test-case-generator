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
package org.smartut.assertion.stable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.statistics.OutputVariable;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.statistics.backend.DebugStatisticsBackend;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.stable.Overload;

public class OverloadSystemTest extends SystemTestBase {

	private final boolean DEFAULT_REPLACE_CALLS = Properties.REPLACE_CALLS;
	private final Properties.JUnitCheckValues DEFAULT_JUNIT_CHECK = Properties.JUNIT_CHECK;
	private final boolean DEFAULT_JUNIT_TESTS = Properties.JUNIT_TESTS;
	private final boolean DEFAULT_PURE_INSPECTORS = Properties.PURE_INSPECTORS;
	private final boolean DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS = Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS;
	private final boolean DEFAULT_SANDBOX = Properties.SANDBOX;

	@Before
	public void before() {
		Properties.SANDBOX = true;
		Properties.REPLACE_CALLS = true;
		Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
		Properties.JUNIT_TESTS = true;
		Properties.PURE_INSPECTORS = true;
		Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = false;
	}

	@After
	public void after() {
		Properties.SANDBOX = DEFAULT_SANDBOX;
		Properties.REPLACE_CALLS = DEFAULT_REPLACE_CALLS;
		Properties.JUNIT_CHECK = DEFAULT_JUNIT_CHECK;
		Properties.JUNIT_TESTS = DEFAULT_JUNIT_TESTS;
		Properties.PURE_INSPECTORS = DEFAULT_PURE_INSPECTORS;
		Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = DEFAULT_JUNIT_CHECK_ON_SEPARATE_PROCESS;
	}

	@Test
	public void testIsOverloaded() throws NoSuchMethodException, SecurityException {
		Method m1 = Overload.class.getMethod("execute", Overload.class, Overload.class);
		Method m2 = Overload.class.getMethod("execute", Overload.class, Object.class);
		
		GenericMethod gm1 = new GenericMethod(m1, Overload.class);
		GenericMethod gm2 = new GenericMethod(m2, Overload.class);
		
		Assert.assertTrue(gm1.isOverloaded());
		Assert.assertTrue(gm2.isOverloaded());
	}
	
	@Test
	public void testIsOverloadedInstance() throws NoSuchMethodException, SecurityException {
		Method m1 = Overload.class.getMethod("execute", Overload.class, Overload.class);
		Method m2 = Overload.class.getMethod("execute", Overload.class, Object.class);
		
		GenericMethod gm1 = new GenericMethod(m1, Overload.class);
		GenericMethod gm2 = new GenericMethod(m2, Overload.class);
		
		TestCase test = new DefaultTestCase();
		
		GenericConstructor gc = new GenericConstructor(Overload.class.getConstructors()[0], Overload.class);
		ConstructorStatement cs = new ConstructorStatement(test, gc, new ArrayList<>());
		VariableReference overloadInstance = test.addStatement(cs);

		ConstructorStatement ocs = new ConstructorStatement(test, new GenericConstructor(Object.class.getConstructors()[0], Object.class), new ArrayList<>());
		VariableReference objectInstance = test.addStatement(ocs);

		List<VariableReference> vars1 = new ArrayList<>();
		vars1.add(overloadInstance);
		vars1.add(overloadInstance);

		List<VariableReference> vars2 = new ArrayList<>();
		vars2.add(overloadInstance);
		vars2.add(objectInstance);

		Assert.assertFalse(gm1.isOverloaded(vars1));
		Assert.assertTrue(gm2.isOverloaded(vars1));
		Assert.assertTrue(gm1.isOverloaded(vars2));
		Assert.assertFalse(gm2.isOverloaded(vars2));
	}
	
	@Test
	public void testOverload() {
		SmartUt smartut = new SmartUt();

		String targetClass = Overload.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.OUTPUT_VARIABLES=""+RuntimeVariable.HadUnstableTests;
		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Map<String, OutputVariable<?>> map = DebugStatisticsBackend.getLatestWritten();
		Assert.assertNotNull(map);
		OutputVariable<?> unstable = map.get(RuntimeVariable.HadUnstableTests.toString());
		Assert.assertNotNull(unstable);
		Assert.assertEquals(Boolean.FALSE, unstable.getValue());
	}

}
