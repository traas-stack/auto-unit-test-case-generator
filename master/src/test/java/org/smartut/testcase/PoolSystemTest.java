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
package org.smartut.testcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.seeding.ObjectPool;
import org.smartut.seeding.ObjectPoolManager;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.numeric.IntPrimitiveStatement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.utils.generic.GenericClass;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.examples.with.different.packagename.pool.ClassDependingOnExceptionClass;
import com.examples.with.different.packagename.pool.DependencyClass;
import com.examples.with.different.packagename.pool.DependencyClassWithException;
import com.examples.with.different.packagename.pool.DependencySubClass;
import com.examples.with.different.packagename.pool.OtherClass;

public class PoolSystemTest extends SystemTestBase {

	private String pools = "";
	
	private double pPool = 0.0;
	
	private long budget = 0;
	
	@Before
	public void storeProperties() {
		pools  = Properties.OBJECT_POOLS;
		pPool  = Properties.P_OBJECT_POOL;
		budget = Properties.SEARCH_BUDGET;
	}
	
	@After
	public void restoreProperties() {
		Properties.OBJECT_POOLS = pools;
		Properties.P_OBJECT_POOL = pPool;
		Properties.SEARCH_BUDGET = budget;
	}
	
	@Test
	public void testPoolDependency() throws IOException {
		SmartUt smartut = new SmartUt();

		String targetClass = DependencyClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.SEARCH_BUDGET = 100000;
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
	}
	
	@Test
	public void testPool() throws IOException {
		File f = File.createTempFile("SmartUtTestPool",null, FileUtils.getTempDirectory());
		String filename = f.getAbsolutePath();
		f.delete();
		System.out.println(filename);
		
		
		
		SmartUt smartut = new SmartUt();

		String targetClass = DependencyClass.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SEARCH_BUDGET = 100000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		ObjectPool pool = ObjectPool.getPoolFromTestSuite(best);
		pool.writePool(filename);
		System.out.println("EvolvedTestSuite:\n" + best);
		resetStaticVariables();
		setDefaultPropertiesForTestCases();

		targetClass = OtherClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 1.0;
		Properties.OBJECT_POOLS = filename;
		Properties.SEARCH_BUDGET = 10000;
		ObjectPoolManager.getInstance().initialisePool();
		//Properties.SEARCH_BUDGET = 50000;

		command = new String[] { "-generateSuite", "-class", targetClass, "-Dobject_pools=" + filename };

		result = smartut.parseCommandLine(command);
		ga = getGAFromResult(result);
		TestSuiteChromosome best2 = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best2);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best2.getCoverage(), 0.001);
		f = new File(filename);
		f.delete();

	}
	
	@Ignore
	@Test
	public void testNoPool() throws IOException {
		SmartUt smartut = new SmartUt();

		String targetClass = OtherClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.0;
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertTrue("Expected non-optimal coverage: ", best.getCoverage() < 1.0);
		// Seems to pass now even without pool...
	}
	
	@Test
	public void testPoolWithSubClass() throws IOException {
		File f = File.createTempFile("SmartUtTestPool",null, FileUtils.getTempDirectory());
		String filename = f.getAbsolutePath();
		f.delete();
		System.out.println(filename);
		
		SmartUt smartut = new SmartUt();

		String targetClass = DependencySubClass.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		// It takes a bit longer to cover the branch here
		Properties.SEARCH_BUDGET = 50000;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		ObjectPool pool = ObjectPool.getPoolFromTestSuite(best);
		pool.writePool(filename);
		System.out.println("EvolvedTestSuite:\n" + best);
		resetStaticVariables();
		setDefaultPropertiesForTestCases();

		targetClass = OtherClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 1.0;
		Properties.OBJECT_POOLS = filename;
		ObjectPoolManager.getInstance().initialisePool();

		command = new String[] { "-generateSuite", "-class", targetClass, "-Dobject_pools=" + filename };

		result = smartut.parseCommandLine(command);

		ga = getGAFromResult(result);
		best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
		f = new File(filename);
		f.delete();

	}
	
	@Test
	public void testPoolWithException() throws IOException, NoSuchMethodException, SecurityException {
		File f = File.createTempFile("SmartUtTestPool",null, FileUtils.getTempDirectory());
		String filename = f.getAbsolutePath();
		f.delete();
		System.out.println(filename);
		
		SmartUt smartut = new SmartUt();

		String targetClass = DependencyClassWithException.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.getTargetClassAndDontInitialise();
		TestCase test = new DefaultTestCase();
		VariableReference instance = test.addStatement(new ConstructorStatement(test, new GenericConstructor(DependencyClassWithException.class.getConstructors()[0], DependencyClassWithException.class),
				new ArrayList<>()));
		VariableReference int42 = test.addStatement(new IntPrimitiveStatement(test, 42));
		GenericMethod foo = new GenericMethod(DependencyClassWithException.class.getMethod("foo", int.class), DependencyClassWithException.class);
		test.addStatement(new MethodStatement(test, foo, instance, Arrays.asList(new VariableReference[] {int42})));
		test.addStatement(new MethodStatement(test, foo, instance, Arrays.asList(new VariableReference[] {int42})));
		test.addStatement(new MethodStatement(test, foo, instance, Arrays.asList(new VariableReference[] {int42})));
		test.addStatement(new MethodStatement(test, foo, instance, Arrays.asList(new VariableReference[] {int42})));
		test.addStatement(new MethodStatement(test, foo, instance, Arrays.asList(new VariableReference[] {int42})));
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		TestSuiteChromosome best = new TestSuiteChromosome();
		best.addTest(test);
		ObjectPool pool = new ObjectPool();
		pool.addSequence(new GenericClass(DependencyClassWithException.class), test);
		pool.writePool(filename);
		System.out.println("EvolvedTestSuite:\n" + best);
		
		resetStaticVariables();
		setDefaultPropertiesForTestCases();
		
		targetClass = ClassDependingOnExceptionClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.8;
		Properties.OBJECT_POOLS = filename;
		ObjectPoolManager.getInstance().initialisePool();
		//Properties.SEARCH_BUDGET = 50000;

		command = new String[] { "-generateSuite", "-class", targetClass};

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
		f = new File(filename);
		f.delete();

	}

	@Ignore // Can now pass even without pool...
	@Test
	public void testNoPoolWithException() throws IOException {
		SmartUt smartut = new SmartUt();

		String targetClass = ClassDependingOnExceptionClass.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.P_OBJECT_POOL = 0.0;
		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);

		Assert.assertTrue("Non-optimal coverage: ", best.getCoverage() < 1.0);

	}
}
