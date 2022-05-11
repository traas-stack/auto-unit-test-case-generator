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
package org.smartut.symbolic.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.smartut.symbolic.TestCaseBuilder;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.variable.VariableReference;
import org.junit.Test;

import com.examples.with.different.packagename.solver.TestCaseFloatAbs;
import com.examples.with.different.packagename.solver.TestCaseFloatMax;
import com.examples.with.different.packagename.solver.TestCaseFloatMin;
import com.examples.with.different.packagename.solver.TestCaseFloatRound;
import com.examples.with.different.packagename.solver.TestCaseFloatTrigonometry;

public abstract class TestSolverMathFloat extends TestSolver {

	private static DefaultTestCase buildTestCaseFloatAbs() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference double0 = tc.appendDoublePrimitive(-Math.PI);

		Method method = TestCaseFloatAbs.class.getMethod("test", double.class);
		tc.appendMethod(null, method, double0);
		return tc.getDefaultTestCase();
	}

	@Test
	public void testAbs() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestCaseFloatAbs();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		Map<String, Object> solution = solve(getSolver(), constraints);
		assertNotNull(solution);
		Double var0 = (Double) solution.get("var0");

		assertTrue(Math.abs(var0) > 0);
	}

	private static DefaultTestCase buildTestCaseFloatTrigonometry() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference double0 = tc.appendDoublePrimitive(-Math.PI);

		Method method = TestCaseFloatTrigonometry.class.getMethod("test", double.class);
		tc.appendMethod(null, method, double0);
		return tc.getDefaultTestCase();
	}

	@Test
	public void testTrigonometry() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestCaseFloatTrigonometry();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		Map<String, Object> solution = solve(getSolver(), constraints);
	}

	private static DefaultTestCase buildTestCaseMax() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference double0 = tc.appendDoublePrimitive(Double.MIN_VALUE);
		VariableReference double1 = tc.appendDoublePrimitive(10);

		Method method = TestCaseFloatMax.class.getMethod("test", double.class, double.class);
		tc.appendMethod(null, method, double0, double1);
		return tc.getDefaultTestCase();
	}

	private static DefaultTestCase buildTestCaseMin() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference double0 = tc.appendDoublePrimitive(Double.MAX_VALUE);
		VariableReference double1 = tc.appendDoublePrimitive(10);

		Method method = TestCaseFloatMin.class.getMethod("test", double.class, double.class);
		tc.appendMethod(null, method, double0, double1);
		return tc.getDefaultTestCase();
	}

	@Test
	public void testMax() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestCaseMax();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		Map<String, Object> solution = solve(getSolver(), constraints);
		assertNotNull(solution);
		Double var0 = (Double) solution.get("var0");
		Double var1 = (Double) solution.get("var1");

		assertEquals(10, Math.max(var0, var1), DELTA);
	}

	@Test
	public void testMin() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestCaseMin();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		Map<String, Object> solution = solve(getSolver(), constraints);
		assertNotNull(solution);
		Double var0 = (Double) solution.get("var0");
		Double var1 = (Double) solution.get("var1");

		assertEquals(10, Math.min(var0, var1), DELTA);
	}

	private static final double DELTA = 1e-15;

	private static DefaultTestCase buildTestCaseRound() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference double0 = tc.appendDoublePrimitive(Math.PI);
		VariableReference int1 = tc.appendIntPrimitive((int) Math.round(Math.PI));

		Method method = TestCaseFloatRound.class.getMethod("test", double.class, int.class);
		tc.appendMethod(null, method, double0, int1);
		return tc.getDefaultTestCase();
	}

	@Test
	public void testRound() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestCaseRound();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		Map<String, Object> solution = solve(getSolver(), constraints);
		assertNotNull(solution);
		Double var0 = (Double) solution.get("var0");
		Long var1 = (Long) solution.get("var1");

		assertEquals(Math.round(var0), var1.intValue());
	}
}
