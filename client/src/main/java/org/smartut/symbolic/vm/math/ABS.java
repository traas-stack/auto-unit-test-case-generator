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
package org.smartut.symbolic.vm.math;

import org.smartut.symbolic.expr.bv.IntegerUnaryExpression;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.fp.RealUnaryExpression;
import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.vm.SymbolicFunction;
import org.smartut.symbolic.vm.SymbolicEnvironment;

public abstract class ABS {

	private static final String ABS_FUNCTION_NAME = "abs";

	public final static class ABS_D extends SymbolicFunction {

		public ABS_D(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ABS_FUNCTION_NAME,
					Types.D2D_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			double res = this.getConcDoubleRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);

			RealValue sym_val;
			if (realExpression.containsSymbolicVariable()) {
				sym_val = new RealUnaryExpression(realExpression, Operator.ABS,
						res);
			} else {
				sym_val = this.getSymbRealRetVal();
			}
			return sym_val;
		}

	}

	public final static class ABS_F extends SymbolicFunction {

		public ABS_F(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ABS_FUNCTION_NAME,
					Types.F2F_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			float res = this.getConcFloatRetVal();
			RealValue realExpression = this.getSymbRealArgument(0);

			RealValue sym_val;
			if (realExpression.containsSymbolicVariable()) {
				sym_val = new RealUnaryExpression(realExpression, Operator.ABS,
						(double) res);
			} else {
				sym_val = this.getSymbRealRetVal();
			}
			return sym_val;
		}
	}

	public final static class ABS_I extends SymbolicFunction {

		public ABS_I(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ABS_FUNCTION_NAME,
					Types.I2I_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			int res = this.getConcIntRetVal();
			IntegerValue intExpression = this.getSymbIntegerArgument(0);
			IntegerValue sym_val;
			if (intExpression.containsSymbolicVariable()) {
				sym_val = new IntegerUnaryExpression(intExpression,
						Operator.ABS, (long) res);
			} else {
				sym_val = this.getSymbIntegerRetVal();
			}
			return sym_val;
		}

	}

	public final static class ABS_L extends SymbolicFunction {

		public ABS_L(SymbolicEnvironment env) {
			super(env, Types.JAVA_LANG_MATH, ABS_FUNCTION_NAME,
					Types.L2L_DESCRIPTOR);
		}

		@Override
		public Object executeFunction() {
			long res = this.getConcLongRetVal();
			IntegerValue intExpression = this.getSymbIntegerArgument(0);
			IntegerValue sym_val;
			if (intExpression.containsSymbolicVariable()) {
				sym_val = new IntegerUnaryExpression(intExpression,
						Operator.ABS, res);
			} else {
				sym_val = this.getSymbIntegerRetVal();
			}
			return sym_val;
		}

	}

}
