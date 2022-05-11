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
package org.smartut.runtime.sandbox;

import java.util.List;

import com.examples.with.different.packagename.sandbox.ReadTimezone;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.junit.JUnitAnalyzer;
import org.smartut.result.TestGenerationResult;
import org.smartut.result.TestGenerationResultBuilder;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Test;

import com.examples.with.different.packagename.sandbox.ReadWriteSystemProperties;

public class ReadWriteSystemPropertiesSystemTest extends SystemTestBase {

	private static final String userDir = System
			.getProperty(ReadWriteSystemProperties.USER_DIR);
	private static final String aProperty = System
			.getProperty(ReadWriteSystemProperties.A_PROPERTY);

	private final boolean DEFAULT_REPLACE_CALLS = Properties.REPLACE_CALLS;

	@After
	public void reset() {
		Properties.REPLACE_CALLS = DEFAULT_REPLACE_CALLS;
	}

	@BeforeClass
	public static void checkStatus() {
		//such property shouldn't exist
		Assert.assertNull(aProperty);
	}

	@Test
	public void testReadLineSeparator() {
		SmartUt smartut = new SmartUt();

		String targetClass = ReadTimezone.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SANDBOX = true;
		Properties.REPLACE_CALLS = true;

		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		double cov = best.getCoverage();
		Assert.assertEquals("Non-optimal coverage: ", 1d, cov, 0.001);

		//now check the JUnit generation
		List<TestCase> list = best.getTests();
		int n = list.size();
		Assert.assertTrue(n > 0);

		TestCaseExecutor.initExecutor(); //needed because it gets pulled down after the search

		try {
			Sandbox.initializeSecurityManagerForSUT();
			JUnitAnalyzer.removeTestsThatDoNotCompile(list);
		} finally {
			Sandbox.resetDefaultSecurityManager();
		}
		Assert.assertEquals(n, list.size());

		TestGenerationResult tgr = TestGenerationResultBuilder.buildSuccessResult();
		String code = tgr.getTestSuiteCode();
		Assert.assertTrue("Test code:\n" + code, code.contains("user.timezone"));
		
		/*
		 * This is tricky. The property 'debug' is read, but it does not exist. 
		 * Ideally, we should still have in the test case a call to be sure the variable
		 * is set to null. But that would lead to a lot of problems :( eg cases
		 * in which we end up in reading hundreds of thousands variables that do not exist
		 */
        Assert.assertFalse("Test code:\n" + code, code.contains("debug"));
	}

	@Test
	public void testNoReplace() {

		SmartUt smartut = new SmartUt();

		String targetClass = ReadWriteSystemProperties.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SANDBOX = true;
		Properties.REPLACE_CALLS = false;

		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		double cov = best.getCoverage();
		//without replace calls, we shouldn't be able to achieve full coverage
		Assert.assertTrue(cov < 1d);
	}

	@Test
	public void testWithReplace() {

		SmartUt smartut = new SmartUt();

		String targetClass = ReadWriteSystemProperties.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.SANDBOX = true;
		Properties.REPLACE_CALLS = true;

		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);

		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
		System.out.println("EvolvedTestSuite:\n" + best);
		double cov = best.getCoverage();
		Assert.assertEquals("Non-optimal coverage: ", 1d, cov, 0.001);

		//now check if properties have been reset to their initial state
		String currentUserDir = System
				.getProperty(ReadWriteSystemProperties.USER_DIR);
		String currentAProperty = System
				.getProperty(ReadWriteSystemProperties.A_PROPERTY);

		Assert.assertEquals(userDir, currentUserDir);
		Assert.assertEquals(aProperty, currentAProperty);

		//now check the JUnit generation
		List<TestCase> list = best.getTests();
		int n = list.size();
		Assert.assertTrue(n > 0);

		TestCaseExecutor.initExecutor(); //needed because it gets pulled down after the search

		try {
			Sandbox.initializeSecurityManagerForSUT();
			for (TestCase tc : list) {
				Assert.assertFalse(tc.isUnstable());
			}

			JUnitAnalyzer.removeTestsThatDoNotCompile(list);
			Assert.assertEquals(n, list.size());
			JUnitAnalyzer.handleTestsThatAreUnstable(list);
			Assert.assertEquals(n, list.size());

			for (TestCase tc : list) {
				Assert.assertFalse(tc.isUnstable());
			}

			Assert.assertEquals(userDir, currentUserDir);
			Assert.assertEquals(aProperty, currentAProperty);
		}  finally {
			Sandbox.resetDefaultSecurityManager();
		}
	}
}
