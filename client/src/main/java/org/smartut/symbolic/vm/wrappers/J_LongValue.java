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

import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class J_LongValue extends SymbolicFunction {

	private static final String LONG_VALUE = "longValue";

	public J_LongValue(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_LONG, LONG_VALUE, Types.TO_LONG);
	}

	@Override
	public Object executeFunction() {
		ReferenceConstant symb_long = this.getSymbReceiver();
		Long conc_long = (Long) this.getConcReceiver();

		long conc_long_value = this.getConcLongRetVal();
		IntegerValue symb_long_value = env.heap
				.getField(Types.JAVA_LANG_LONG, SymbolicHeap.$LONG_VALUE,
						conc_long, symb_long, conc_long_value);
		return symb_long_value;
	}

}
