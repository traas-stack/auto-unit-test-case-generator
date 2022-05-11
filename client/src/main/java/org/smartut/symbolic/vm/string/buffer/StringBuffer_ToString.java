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
package org.smartut.symbolic.vm.string.buffer;

import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.str.StringValue;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class StringBuffer_ToString extends SymbolicFunction {

	private static final String TO_STRING = "toString";

	public StringBuffer_ToString(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_STRING_BUFFER, TO_STRING,
				Types.TO_STR_DESCRIPTOR);
	}

	@Override
	public Object executeFunction() {
		ReferenceConstant symb_str_buffer = this.getSymbReceiver();
		StringBuffer conc_str_buffer = (StringBuffer) this.getConcReceiver();

		// retrieve symbolic value from heap
		String conc_value = conc_str_buffer.toString();
		StringValue symb_value = env.heap.getField(
				Types.JAVA_LANG_STRING_BUFFER,
				SymbolicHeap.$STRING_BUFFER_CONTENTS, conc_str_buffer,
				symb_str_buffer, conc_value);

		String conc_ret_val = (String) this.getConcRetVal();
		ReferenceConstant symb_ret_val = (ReferenceConstant) this.getSymbRetVal();

		env.heap.putField(Types.JAVA_LANG_STRING, SymbolicHeap.$STRING_VALUE,
				conc_ret_val, symb_ret_val, symb_value);

		// return symbolic value
		return symb_ret_val;
	}

}
