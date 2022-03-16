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

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerUnaryExpression;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;

public final class Character_getNumericValue extends SymbolicFunction {

	private final static String GET_NUMERIC_VALUE = "getNumericValue";

	public Character_getNumericValue(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_CHARACTER, GET_NUMERIC_VALUE, Types.C_TO_I);
	}

	@Override
	public Object executeFunction() {

		IntegerValue charValueExpr = this.getSymbIntegerArgument(0);
		int res = this.getConcIntRetVal();

		if (charValueExpr.containsSymbolicVariable()) {

			IntegerUnaryExpression getNumericValueExpr = new IntegerUnaryExpression(
					charValueExpr, Operator.GETNUMERICVALUE, (long) res);
			return getNumericValueExpr;

		} else {
			return this.getSymbIntegerRetVal();
		}
	}

}
