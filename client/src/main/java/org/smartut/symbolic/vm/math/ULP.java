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
package org.smartut.symbolic.vm.math;

import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.fp.RealUnaryExpression;
import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicEnvironment;

public abstract class ULP {

	private static final String ULP = "ulp";

	public final static class ULP_D extends SymbolicFunction {

		public ULP_D(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ULP, Types.D2D_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			double res = this.getConcDoubleRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);
			RealValue ulpExpr;
			if (realExpression.containsSymbolicVariable()) {
				Operator op = Operator.ULP;
				ulpExpr = new RealUnaryExpression(realExpression, op, res);
			} else {
				ulpExpr = this.getSymbRealRetVal();
			}
			return ulpExpr;
		}

	}

	public final static class ULP_F extends SymbolicFunction {

		public ULP_F(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ULP, Types.F2F_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			float res = this.getConcFloatRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);
			RealValue ulpExpr;
			if (realExpression.containsSymbolicVariable()) {
				Operator op = Operator.ULP;
				ulpExpr = new RealUnaryExpression(realExpression, op,
						(double) res);
			} else {
				ulpExpr = this.getSymbRealRetVal();
			}
			return ulpExpr;
		}

	}

}
