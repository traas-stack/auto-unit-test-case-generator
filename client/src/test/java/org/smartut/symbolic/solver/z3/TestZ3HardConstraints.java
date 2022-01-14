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
package org.smartut.symbolic.solver.z3;

import static org.smartut.symbolic.solver.TestSolver.solve;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.smartut.symbolic.TestCaseBuilder;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.solver.DefaultTestCaseConcolicExecutor;
import org.smartut.symbolic.solver.SolverTimeoutException;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.variable.VariableReference;
import org.junit.Test;

import com.examples.with.different.packagename.concolic.HardConstraints;

public class TestZ3HardConstraints extends TestZ3 {

	@Test
	public void test0() throws SecurityException, NoSuchMethodException, SolverTimeoutException {
		Z3Solver solver = new Z3Solver();

		DefaultTestCase tc = buildTestCase0();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		assertFalse(constraints.isEmpty());
		Map<String, Object> solution = solve(solver, constraints);
		assertNotNull(solution);

		Long int0 = (Long) solution.get("var0");
		assertEquals(13075, int0.intValue());
	}
	
	@Test
	public void test1() throws SecurityException, NoSuchMethodException, SolverTimeoutException {
		Z3Solver solver = new Z3Solver();

		DefaultTestCase tc = buildTestCase1();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		assertFalse(constraints.isEmpty());
		Map<String, Object> solution = solve(solver, constraints);
		assertNotNull(solution);

		Long int0 = (Long) solution.get("var0");
		assertEquals(25100, int0.intValue());
	}
	
	@Test
	public void test2() throws SecurityException, NoSuchMethodException, SolverTimeoutException {
		Z3Solver solver = new Z3Solver();

		DefaultTestCase tc = buildTestCase2();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		assertFalse(constraints.isEmpty());
		Map<String, Object> solution = solve(solver, constraints);
		assertNull(solution);
	}

	private DefaultTestCase buildTestCase0() throws NoSuchMethodException, SecurityException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference int0 = tc.appendIntPrimitive(13075);
		Method method = HardConstraints.class.getMethod("test0", int.class);
		tc.appendMethod(null, method, int0);
		return tc.getDefaultTestCase();
	}

	private DefaultTestCase buildTestCase1() throws NoSuchMethodException, SecurityException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference int0 = tc.appendIntPrimitive(25100);
		Method method = HardConstraints.class.getMethod("test1", int.class);
		tc.appendMethod(null, method, int0);
		return tc.getDefaultTestCase();
	}

	private DefaultTestCase buildTestCase2() throws NoSuchMethodException, SecurityException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference float0 = tc.appendFloatPrimitive(3.1415998935699463f);
		Method method = HardConstraints.class.getMethod("test2", float.class);
		tc.appendMethod(null, method, float0);
		return tc.getDefaultTestCase();
	}
}
