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
package org.smartut.symbolic.expr.bv;

import java.util.HashSet;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.ConstraintTooLongException;
import org.smartut.symbolic.DSEStats;
import org.smartut.symbolic.expr.AbstractExpression;
import org.smartut.symbolic.expr.Expression;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RealComparison extends AbstractExpression<Long> implements
        IntegerValue {
	private static final long serialVersionUID = 1L;

	protected static final Logger log = LoggerFactory.getLogger(RealComparison.class);

	/**
	 * <p>
	 * Constructor for RealComparison.
	 * </p>
	 * 
	 * @param left
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param right
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param con
	 *            a {@link java.lang.Long} object.
	 */
	public RealComparison(Expression<Double> left, Expression<Double> right, Long con) {
		super(con, 1 + left.getSize() + right.getSize(), left.containsSymbolicVariable()
		        || right.containsSymbolicVariable());
		this.left = left;
		this.right = right;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH) {
			DSEStats.getInstance().reportConstraintTooLong(getSize());
			throw new ConstraintTooLongException(getSize());
		}
	}

	private final Expression<Double> left;
	private final Expression<Double> right;

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RealComparison) {
			RealComparison other = (RealComparison) obj;
			return this.left.equals(other.left) && this.right.equals(other.right);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.left.hashCode() + this.right.hashCode();
	}

	/**
	 * <p>
	 * getRightOperant
	 * </p>
	 * 
	 * @return a {@link org.smartut.symbolic.expr.Expression} object.
	 */
	public Expression<Double> getRightOperant() {
		return right;
	}

	/**
	 * <p>
	 * getLeftOperant
	 * </p>
	 * 
	 * @return a {@link org.smartut.symbolic.expr.Expression} object.
	 */
	public Expression<Double> getLeftOperant() {
		return left;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "(" + left + " cmp " + right + ")";
	}

	@Override
	public Set<Variable<?>> getVariables() {
		Set<Variable<?>> variables = new HashSet<>();
		variables.addAll(this.left.getVariables());
		variables.addAll(this.right.getVariables());
		return variables;
	}

	@Override
	public Set<Object> getConstants() {
		Set<Object> result = new HashSet<>();
		result.addAll(left.getConstants());
		result.addAll(right.getConstants());
		return result;
	}

	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
}