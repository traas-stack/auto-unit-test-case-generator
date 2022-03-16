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

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.smartut.symbolic.vm.ExpressionFactory;
import org.junit.Test;

public class TestPatternSearch extends RandomizedTC {

	@Test
	public void testMatcherMatches() throws SolverEmptyQueryException {

		String input = "random_value";
		String format = "(\\d+)-(\\d\\d)-(\\d)";
		// String format = "^(\\d+)-(\\d\\d)-(\\d)$";

		StringVariable var0 = new StringVariable("var0", input);

		StringConstant symb_regex = ExpressionFactory
				.buildNewStringConstant(format);
		StringBinaryComparison strComp = new StringBinaryComparison(symb_regex,
				Operator.PATTERNMATCHES, var0, 0L);

		StringConstraint constraint = new StringConstraint(strComp,
				Comparator.NE, new IntegerConstant(0));

		List<Constraint<?>> constraints = Collections
				.<Constraint<?>> singletonList(constraint);

		try {
			SmartUtSolver solver = new SmartUtSolver();
			SolverResult result = solver.solve(constraints);
			assertTrue(result.isSAT());
			
			Map<String,Object>model = result.getModel();
			
			String var0_value = (String) model.get("var0");

			Pattern pattern = Pattern.compile(format);
			Matcher matcher = pattern.matcher(var0_value);
			assertTrue(matcher.matches());
		} catch (SolverTimeoutException e) {
			fail();
		}

	}

}
