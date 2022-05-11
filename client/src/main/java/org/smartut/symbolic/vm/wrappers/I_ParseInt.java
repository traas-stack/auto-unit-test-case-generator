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
package org.smartut.symbolic.vm.wrappers;

import org.smartut.symbolic.expr.Comparator;
import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.StringToIntegerCast;
import org.smartut.symbolic.expr.bv.StringUnaryToIntegerExpression;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class I_ParseInt extends SymbolicFunction {

	private static final String PARSE_INT = "parseInt";

	public I_ParseInt(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_INTEGER, PARSE_INT,
				Types.STR_TO_INT_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {

		ReferenceConstant symb_string_ref = (ReferenceConstant) this
				.getSymbArgument(0);
		String conc_string = (String) this.getConcArgument(0);

		int conc_integer = this.getConcIntRetVal();

		StringValue symb_string_value = env.heap.getField(
				org.smartut.symbolic.vm.regex.Types.JAVA_LANG_STRING,
				SymbolicHeap.$STRING_VALUE, conc_string, symb_string_ref,
				conc_string);

		long longValue = conc_integer;

		StringToIntegerCast parse_int_value = new StringToIntegerCast(
				symb_string_value, longValue);

		return parse_int_value;
	}

	@Override
	public IntegerConstraint beforeExecuteFunction() {
		String conc_string = (String) this.getConcArgument(0);

		try {
			Integer.parseInt(conc_string);
			return null;
		} catch (NumberFormatException ex) {

			ReferenceConstant symb_string_ref = (ReferenceConstant) this
					.getSymbArgument(0);
			StringValue symb_string_value = env.heap.getField(
					org.smartut.symbolic.vm.regex.Types.JAVA_LANG_STRING,
					SymbolicHeap.$STRING_VALUE, conc_string, symb_string_ref,
					conc_string);

			long conV = 0;
			StringUnaryToIntegerExpression isIntegerExpression = new StringUnaryToIntegerExpression(
					symb_string_value, Operator.IS_INTEGER, conV);

			IntegerConstraint integerConstraint = new IntegerConstraint(
					isIntegerExpression, Comparator.EQ, new IntegerConstant(0));
			return integerConstraint;
		}

	}

}
