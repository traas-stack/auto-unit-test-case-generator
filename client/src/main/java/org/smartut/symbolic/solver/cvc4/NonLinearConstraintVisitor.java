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
package org.smartut.symbolic.solver.cvc4;

import org.smartut.symbolic.expr.ConstraintVisitor;
import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.RealConstraint;
import org.smartut.symbolic.expr.StringConstraint;

final class NonLinearConstraintVisitor implements ConstraintVisitor<Boolean, Void> {

	private final NonLinearExpressionVisitor exprVisitor = new NonLinearExpressionVisitor();

	@Override
	public Boolean visit(IntegerConstraint n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(exprVisitor, null);
		if (left_ret_val) {
			return true;
		}

		Boolean right_ret_val = n.getRightOperand().accept(exprVisitor, null);
		if (right_ret_val) {
			return right_ret_val;
		}

		return false;
	}

	@Override
	public Boolean visit(RealConstraint n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(exprVisitor, null);
		if (left_ret_val) {
			return true;
		}

		Boolean right_ret_val = n.getRightOperand().accept(exprVisitor, null);
		if (right_ret_val) {
			return right_ret_val;
		}

		return false;
	}

	@Override
	public Boolean visit(StringConstraint n, Void arg) {
		Boolean left_ret_val = n.getLeftOperand().accept(exprVisitor, null);
		if (left_ret_val) {
			return true;
		}

		Boolean right_ret_val = n.getRightOperand().accept(exprVisitor, null);
		if (right_ret_val) {
			return right_ret_val;
		}

		return false;
	}

}
