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
package org.smartut.symbolic.vm;

import org.smartut.symbolic.expr.Comparator;
import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.bv.IntegerValue;

/**
 * 
 * @author galeotti
 *
 */
public abstract class ConstraintFactory {

	public static IntegerConstraint eq(IntegerValue left,
			IntegerValue right) {
		return new IntegerConstraint(left, Comparator.EQ, right);
	}

	public static IntegerConstraint neq(IntegerValue left,
			IntegerValue right) {
		return new IntegerConstraint(left, Comparator.NE, right);
	}

	public static IntegerConstraint lt(IntegerValue left,
			IntegerValue right) {
		return new IntegerConstraint(left, Comparator.LT, right);

	}

	public static IntegerConstraint lte(IntegerValue left,
			IntegerValue right) {
		return new IntegerConstraint(left, Comparator.LE, right);

	}
	public static IntegerConstraint gte(IntegerValue left,
			IntegerValue right) {
		return new IntegerConstraint(left, Comparator.GE, right);

	}
}
