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
package org.smartut.setup.callgraph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.setup.DependencyAnalysis;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DependencyAnalysisTestComplex {

	@BeforeClass
	public static void initialize() {
		Properties.TARGET_CLASS = "com.examples.with.different.packagename.context.EntryPointsClass";
		Properties.CRITERION = new Criterion[1];
		Properties.CRITERION[0]=Criterion.IBRANCH;
		List<String> classpath = new ArrayList<>();
		String cp = System.getProperty("user.dir") + "/target/test-classes";
		classpath.add(cp);
		ClassPathHandler.getInstance().addElementToTargetProjectClassPath(cp);
		try {
			DependencyAnalysis
					.analyzeClass(
							"com.examples.with.different.packagename.context.EntryPointsClass",
							classpath);
		} catch (ClassNotFoundException | RuntimeException e) {
			Assert.fail();
		}
	}

	@Test
	public void test1levelContext() {
		String context1 = DependencyAnalysis
				.getCallGraph()
				.getAllContextsFromTargetClass(
						"com.examples.with.different.packagename.context.SubClass",
						"checkFiftneen(I)Z").toString();
		assertEquals(
				context1,
				"[com.examples.with.different.packagename.context.EntryPointsClass:dosmt(I)V com.examples.with.different.packagename.context.SubClass:checkFiftneen(I)Z]");
	}
	
	/**
	 * test level 2 context masked by an abtract class
	 */
	@Test
	public void test2levelContext() {
		String context2 = DependencyAnalysis
				.getCallGraph()
				.getAllContextsFromTargetClass(
						"com.examples.with.different.packagename.context.SubClass",
						"bla(I)Z").toString();
		System.out.println(DependencyAnalysis
				.getCallGraph().getViewOfCurrentMethods());
		assertEquals(
				context2,
				"[com.examples.with.different.packagename.context.EntryPointsClass:dosmt(I)V com.examples.with.different.packagename.context.SubClass:checkFiftneen(I)Z com.examples.with.different.packagename.context.SubClass:bla(I)Z]");
	}

	/**
	 * test level 3 context masked by an abstract class and an interface
	 */
	@Test
	public void test3levelContext() {
		String context2 = DependencyAnalysis
				.getCallGraph()
				.getAllContextsFromTargetClass(
						"com.examples.with.different.packagename.context.SubSubClass",
						"innermethod(I)Z").toString();
		System.out.println(DependencyAnalysis
				.getCallGraph().getViewOfCurrentMethods());
		assertEquals(
				context2,
				"[com.examples.with.different.packagename.context.EntryPointsClass:dosmt(I)V com.examples.with.different.packagename.context.SubClass:checkFiftneen(I)Z com.examples.with.different.packagename.context.SubClass:bla(I)Z com.examples.with.different.packagename.context.SubSubClass:innermethod(I)Z]");
	}
	
	//

}