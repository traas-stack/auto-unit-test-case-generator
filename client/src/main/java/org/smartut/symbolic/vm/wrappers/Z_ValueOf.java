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

import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicHeap;

public final class Z_ValueOf extends SymbolicFunction {

	private static final String VALUE_OF = "valueOf";

	public Z_ValueOf(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_BOOLEAN, VALUE_OF, Types.Z_TO_BOOLEAN);
	}

	@Override
	public Object executeFunction() {
		IntegerValue int_value = this.getSymbIntegerArgument(0);
		ReferenceConstant symb_boolean = (ReferenceConstant) this.getSymbRetVal();
		Boolean conc_boolean = (Boolean) this.getConcRetVal();
		env.heap.putField(Types.JAVA_LANG_BOOLEAN, SymbolicHeap.$BOOLEAN_VALUE,
				conc_boolean, symb_boolean, int_value);
		return symb_boolean;
	}

}
