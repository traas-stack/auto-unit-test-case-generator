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
package org.smartut.symbolic.vm.string.buffer;

import java.util.ArrayList;
import java.util.Collections;

import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringMultipleExpression;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class StringBuffer_SetLength extends SymbolicFunction {

	private static final String SET_LENGTH = "setLength";

	public StringBuffer_SetLength(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING_BUFFER, SET_LENGTH,
				Types.INT_TO_VOID_DESCRIPTOR);
	}

	private String pre_conc_value = null;

	@Override
	public Object executeFunction() {
		ReferenceConstant symb_str_buffer = this.getSymbReceiver();
		StringBuffer conc_str_buffer = (StringBuffer) this.getConcReceiver();

		IntegerValue newSymbLength = this.getSymbIntegerArgument(0);
		int newConcLength = this.getConcIntArgument(0);

		// retrieve symbolic value from heap
		String conc_value = conc_str_buffer.toString();
		StringValue symb_value = env.heap.getField(
				Types.JAVA_LANG_STRING_BUFFER,
				SymbolicHeap.$STRING_BUFFER_CONTENTS, conc_str_buffer,
				symb_str_buffer, pre_conc_value);

		if (symb_value.containsSymbolicVariable()
				|| newSymbLength.containsSymbolicVariable()) {

			StringValue new_symb_value = null;
			if (!newSymbLength.containsSymbolicVariable() && newConcLength == 0) {
				// StringBuffer contents equals to "" string
				new_symb_value = new StringConstant("");
			} else {
				// StringBuffer contents equ
				new_symb_value = new StringMultipleExpression(symb_value,
						Operator.SUBSTRING, new IntegerConstant(0),
						new ArrayList<>(Collections
								.singletonList(newSymbLength)),
						conc_value);
			}

			env.heap.putField(Types.JAVA_LANG_STRING_BUFFER,
					SymbolicHeap.$STRING_BUFFER_CONTENTS, conc_str_buffer,
					symb_str_buffer, new_symb_value);

		}

		// return void
		return null;
	}

	@Override
	public IntegerConstraint beforeExecuteFunction() {
		StringBuffer conc_str_buffer = (StringBuffer) this.getConcReceiver();
		if (conc_str_buffer != null) {
			pre_conc_value = conc_str_buffer.toString();
		} else {
			pre_conc_value = null;
		}
		return null;
	}
}
