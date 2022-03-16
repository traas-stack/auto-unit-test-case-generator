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
package org.smartut.symbolic.expr.bv;

import java.util.HashSet;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.ConstraintTooLongException;
import org.smartut.symbolic.DSEStats;
import org.smartut.symbolic.expr.AbstractExpression;
import org.smartut.symbolic.expr.Cast;
import org.smartut.symbolic.expr.Expression;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Variable;

public final class RealToIntegerCast extends AbstractExpression<Long> implements
        Cast<Double>, IntegerValue {

	private static final long serialVersionUID = 1L;

	private final Expression<Double> expr;

	/**
	 * <p>
	 * Constructor for RealToIntegerCast.
	 * </p>
	 * 
	 * @param _expr
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param _concValue
	 *            a {@link java.lang.Long} object.
	 */
	public RealToIntegerCast(Expression<Double> _expr, Long _concValue) {
		super(_concValue, 1 + _expr.getSize(), _expr.containsSymbolicVariable());
		this.expr = _expr;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH) {
			DSEStats.getInstance().reportConstraintTooLong(getSize());
			throw new ConstraintTooLongException(getSize());
		}
	}

	/** {@inheritDoc} */
	@Override
	public Expression<Double> getArgument() {
		return expr;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "((INT)" + expr + ")";
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RealToIntegerCast) {
			RealToIntegerCast other = (RealToIntegerCast) obj;
			return this.expr.equals(other.expr);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.expr.hashCode();
	}

	@Override
	public Set<Variable<?>> getVariables() {
        Set<Variable<?>> variables = new HashSet<>(this.expr.getVariables());
		return variables;
	}

	@Override
	public Set<Object> getConstants() {
		return expr.getConstants();
	}
	
	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
}
