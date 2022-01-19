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
package org.smartut.symbolic.solver.avm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.smartut.Properties;
import org.smartut.Properties.LocalSearchBudgetType;
import org.smartut.RandomizedTC;
import org.smartut.symbolic.expr.Comparator;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.StringConstraint;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringVariable;
import org.smartut.symbolic.solver.SolverEmptyQueryException;
import org.smartut.symbolic.solver.SolverResult;
import org.smartut.symbolic.solver.SolverTimeoutException;
import org.junit.Test;

public class TestConstraintSolver2 extends RandomizedTC {

	private static final String INIT_STRING = "abc_e";
	private static final String EXPECTED_STRING = "abcbb";

	private static Collection<Constraint<?>> buildConstraintSystem() {

		StringVariable var0 = new StringVariable("var0", INIT_STRING);

		StringConstant const0 = new StringConstant(EXPECTED_STRING);

		StringBinaryComparison strEqual = new StringBinaryComparison(var0, Operator.EQUALS, const0, (long) 0);

		IntegerConstant const_zero = new IntegerConstant(0);

		StringConstraint constr1 = new StringConstraint(strEqual, Comparator.NE, const_zero);

		return Arrays.<Constraint<?>> asList(constr1);
	}

	@Test
	public void test() throws SolverEmptyQueryException {
		Properties.LOCAL_SEARCH_BUDGET = 100; // 5000000000000L; TODO - ??
		Properties.LOCAL_SEARCH_BUDGET_TYPE = LocalSearchBudgetType.FITNESS_EVALUATIONS;

		Collection<Constraint<?>> constraints = buildConstraintSystem();

		System.out.println("Constraints:");
		for (Constraint<?> c : constraints) {
			System.out.println(c.toString());
		}

		System.out.println("");
		System.out.println("Initial: " + INIT_STRING);

		SmartUtSolver solver = new SmartUtSolver();
		try {
			SolverResult solverResult = solver.solve(constraints);
			assertTrue(solverResult.isSAT());
			Map<String, Object> model = solverResult.getModel();
			assertNotNull(model);

			Object var0 = model.get("var0");
			System.out.println("Expected: " + EXPECTED_STRING);
			System.out.println("Found: " + var0);

			assertEquals(EXPECTED_STRING, var0);
		} catch (SolverTimeoutException e) {
			fail();
		}

	}

	public void test2() {
		String l1 = "hello";
		String l2 = "world";
		if (l1.equals(l2)) {
			System.out.println("xx");
		}
	}

}
