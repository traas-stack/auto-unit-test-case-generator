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
package org.smartut.symbolic.vm.wrappers;

import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class F_ValueOf extends SymbolicFunction {

	private static final String VALUE_OF = "valueOf";

	public F_ValueOf(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_FLOAT, VALUE_OF, Types.F_TO_FLOAT);
	}

	@Override
	public Object executeFunction() {
		RealValue real_value = this.getSymbRealArgument(0);
		ReferenceConstant symb_float = (ReferenceConstant) this.getSymbRetVal();
		Float conc_float = (Float) this.getConcRetVal();
		env.heap.putField(Types.JAVA_LANG_FLOAT, SymbolicHeap.$FLOAT_VALUE,
				conc_float, symb_float, real_value);
		return symb_float;
	}

}
