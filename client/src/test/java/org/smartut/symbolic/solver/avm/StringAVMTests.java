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
package org.smartut.symbolic.solver.avm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.smartut.Properties;
import org.smartut.RandomizedTC;
import org.smartut.symbolic.expr.Comparator;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.StringConstraint;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringVariable;
import org.smartut.symbolic.solver.SolverTimeoutException;
import org.smartut.symbolic.vm.ExpressionFactory;
import org.junit.Test;

public class StringAVMTests extends RandomizedTC {

	private List<Constraint<?>> getPatternConstraint(StringVariable var, String format){
		StringConstant symb_regex = ExpressionFactory.buildNewStringConstant(format);
		StringBinaryComparison strComp = new StringBinaryComparison(symb_regex, Operator.PATTERNMATCHES, var, 0L);
		StringConstraint constraint = new StringConstraint(strComp,Comparator.NE, new IntegerConstant(0));
		List<Constraint<?>> constraints = Collections.<Constraint<?>> singletonList(constraint);
		return constraints;
	}
	
	@Test
	public void testIssueWithOptional() throws SolverTimeoutException{
		String name = "addd";
		StringVariable var = new StringVariable(name, "");
		
		String format = "a.?c";
		List<Constraint<?>> constraints = getPatternConstraint(var,format);
				
		long start_time = System.currentTimeMillis();
		long timeout = Properties.DSE_CONSTRAINT_SOLVER_TIMEOUT_MILLIS;

		StringAVM avm = new StringAVM(var,constraints,start_time,timeout);
		boolean succeded = avm.applyAVM();
		assertTrue(succeded);
	}
	
	@Test
	public void testSimpleRegexThreeDigits() throws SolverTimeoutException{
		String name = "foo";
		StringVariable var = new StringVariable(name, "");
		
		String format = "\\d\\d\\d";
		List<Constraint<?>> constraints = getPatternConstraint(var,format);
		
		long start_time = System.currentTimeMillis();
		long timeout = Properties.DSE_CONSTRAINT_SOLVER_TIMEOUT_MILLIS;

		StringAVM avm = new StringAVM(var,constraints,start_time,timeout);
		boolean succeded = avm.applyAVM();
		assertTrue(succeded);
		
		String result = var.getConcreteValue();
		Integer value = Integer.parseInt(result);
		assertTrue("Value="+result, value>=0 && value<=999);
	}
	
	@Test
	public void testInsertLeft() throws SolverTimeoutException{
		String name = "foo";
		String start = "abc";
		StringVariable var = new StringVariable(name, start);
		
		String format = "\\d\\d\\d"+start;
		List<Constraint<?>> constraints = getPatternConstraint(var,format);
		
		long start_time = System.currentTimeMillis();
		long timeout = Properties.DSE_CONSTRAINT_SOLVER_TIMEOUT_MILLIS;
		StringAVM avm = new StringAVM(var,constraints, start_time, timeout);
		boolean succeded = avm.applyAVM();
		assertTrue(succeded);
		
		String result = var.getConcreteValue();
        assertEquals("Length=" + result.length(), 6, result.length());
		assertTrue(result, result.endsWith(start));
	}
	
	@Test
	public void testInsertRight() throws SolverTimeoutException{
		String name = "foo";
		String start = "abc";
		StringVariable var = new StringVariable(name, start);
		
		String format = start+"\\d\\d\\d";
		List<Constraint<?>> constraints = getPatternConstraint(var,format);
		
		long start_time = System.currentTimeMillis();
		long timeout = Properties.DSE_CONSTRAINT_SOLVER_TIMEOUT_MILLIS;

		StringAVM avm = new StringAVM(var,constraints,start_time,timeout);
		boolean succeded = avm.applyAVM();
		assertTrue(succeded);
		
		String result = var.getConcreteValue();
        assertEquals("Length=" + result.length(), 6, result.length());
		assertTrue(result, result.startsWith(start));
	}
	
}
