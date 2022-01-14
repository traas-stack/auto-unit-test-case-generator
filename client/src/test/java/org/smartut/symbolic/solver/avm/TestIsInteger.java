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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.smartut.RandomizedTC;
import org.smartut.symbolic.expr.Comparator;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.StringUnaryToIntegerExpression;
import org.smartut.symbolic.expr.str.StringVariable;
import org.smartut.symbolic.solver.SolverEmptyQueryException;
import org.smartut.symbolic.solver.SolverResult;
import org.smartut.symbolic.solver.SolverTimeoutException;
import org.junit.Test;

public class TestIsInteger extends RandomizedTC {

	@Test
	public void testIsInteger() throws SolverEmptyQueryException {

		List<Constraint<?>> constraints = new ArrayList<>();
		constraints.add(new IntegerConstraint(
				new StringUnaryToIntegerExpression(new StringVariable("var0",
						"hello"), Operator.IS_INTEGER, 0L), Comparator.NE,
				new IntegerConstant(0)));

		SmartUtSolver solver = new SmartUtSolver();
		try {
			SolverResult result = solver.solve(constraints);
			assertTrue(result.isSAT());
		} catch (SolverTimeoutException e) {
			fail();
		}
	}
}
