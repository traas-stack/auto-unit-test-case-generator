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
package org.smartut.symbolic.vm.string;

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.ExpressionFactory;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class Matches extends SymbolicFunction {

	private static final String MATCHES = "matches";

	public Matches(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING, MATCHES,
				Types.STR_TO_BOOL_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {

		// receiver
		ReferenceConstant symb_receiver = this.getSymbReceiver();
		String conc_receiver = (String) this.getConcReceiver();

		// argument
		String conc_argument = (String) this.getConcArgument(0);

		StringValue right_expr = env.heap.getField(Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_receiver, symb_receiver,
				conc_receiver);

		// return val
		boolean res = this.getConcBooleanRetVal();

		if (right_expr.containsSymbolicVariable()) {
			StringConstant left_expr = ExpressionFactory
					.buildNewStringConstant(conc_argument);
			int conV = res ? 1 : 0;

			StringBinaryComparison strBExpr = new StringBinaryComparison(left_expr,
					Operator.PATTERNMATCHES, right_expr, (long) conV);

			return strBExpr;
		}

		return this.getSymbIntegerRetVal();
	}
}
