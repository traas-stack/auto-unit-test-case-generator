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
package org.smartut.runtime.classhandling;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertFalse;

/**
 * @author Jose Rojas
 */
public class ClassWithProtectedConstructorSystemTest extends SystemTestBase {

	private Path testPath;

	@Before
	public void setupTempDir() throws IOException {
		Properties.JUNIT_TESTS = true;
		testPath = Files.createTempDirectory("foobar");
		Properties.TEST_DIR = testPath.toString();
	}

	@Test
	public void shouldNotGenerateTryCatchForIllegalAccessException() throws IOException {

		// run SmartUt
		SmartUt smartut = new SmartUt();
		String targetClass = com.examples.with.different.packagename.listclasses.ClassWithProtectedMethods.class.getCanonicalName();
		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		Object result = smartut.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

		String name = targetClass.substring(targetClass.lastIndexOf(".") + 1) + Properties.JUNIT_SUFFIX;

		// check that the test suite was created
		String junitFile = Properties.TEST_DIR + File.separatorChar +
				Properties.CLASS_PREFIX.replace('.', File.separatorChar) + File.separatorChar +
				name + ".java";
		Path path = Paths.get(junitFile);
		Assert.assertTrue("Test Suite does not exist: "+path, Files.exists(path));
		System.out.println(path.toString());

		String testCode = new String(Files.readAllBytes(path));
		Files.delete(path);
		System.out.println(testCode);
		assertFalse("IllegalAccessException should not occur", testCode.contains("catch(IllegalAccessException e)"));
	}

	@After
	public void removeTempDir() throws IOException {
		Files.walkFileTree(testPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
