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
package org.smartut.symbolic;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collection;

import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.solver.DefaultTestCaseConcolicExecutor;
import org.smartut.symbolic.solver.SolverTimeoutException;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.variable.VariableReference;
import org.junit.Test;

import com.examples.with.different.packagename.solver.TestCaseReader;

public abstract class TestStringReader {

	private static DefaultTestCase buildTestStringReader() throws SecurityException, NoSuchMethodException {
		TestCaseBuilder tc = new TestCaseBuilder();
		VariableReference string0 = tc.appendStringPrimitive("Here is Ramon");

		Method method = TestCaseReader.class.getMethod("test", String.class);
		tc.appendMethod(null, method, string0);
		return tc.getDefaultTestCase();
	}

	@Test
	public void testStringReader() throws SecurityException, NoSuchMethodException, SolverTimeoutException {

		DefaultTestCase tc = buildTestStringReader();
		Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
		assertTrue(!constraints.isEmpty());
	}

}
