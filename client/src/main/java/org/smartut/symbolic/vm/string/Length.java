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
import org.smartut.symbolic.expr.bv.StringUnaryToIntegerExpression;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class Length extends SymbolicFunction {

	private static final String LENGTH = "length";

	public Length(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING, LENGTH, Types.TO_INT_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {
		ReferenceConstant symb_str = this.getSymbReceiver();
		String conc_str = (String) this.getConcReceiver();
		int res = this.getConcIntRetVal();

		StringValue string_expr = env.heap.getField(Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_str, symb_str, conc_str);

		if (string_expr.containsSymbolicVariable()) {
			StringUnaryToIntegerExpression strUnExpr = new StringUnaryToIntegerExpression(
					string_expr, Operator.LENGTH, (long) res);
			return strUnExpr;
		}

		return this.getSymbIntegerRetVal();
	}
}
