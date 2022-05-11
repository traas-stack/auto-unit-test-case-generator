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

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.bv.IntegerUnaryExpression;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.symbolic.vm.SymbolicFunction;

public final class Character_isLetter extends SymbolicFunction {

	private final static String IS_LETTER = "isLetter";

	public Character_isLetter(SymbolicEnvironment env) {
		super(env, Types.JAVA_LANG_CHARACTER, IS_LETTER, Types.C_TO_Z);
	}

	@Override
	public Object executeFunction() {
		IntegerValue charValueExpr = this.getSymbIntegerArgument(0);
		boolean res = this.getConcBooleanRetVal();

		if (charValueExpr.containsSymbolicVariable()) {

			long conV = res ? 1 : 0;

			IntegerUnaryExpression is_letter_expr = new IntegerUnaryExpression(
					charValueExpr, Operator.ISLETTER, conV);
			return is_letter_expr;
		} else {

			return this.getSymbIntegerRetVal();
		}
	}

}
