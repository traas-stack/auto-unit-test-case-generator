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
import org.smartut.symbolic.expr.BinaryExpression;
import org.smartut.symbolic.expr.Expression;
import org.smartut.symbolic.expr.ExpressionEvaluator;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Operator;
import org.smartut.symbolic.expr.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * StringBinaryExpression class.
 * </p>
 * 
 * @author krusev
 */
public final class StringBinaryToIntegerExpression extends
		AbstractExpression<Long> implements IntegerValue,
		BinaryExpression<String> {

	private static final long serialVersionUID = -986689442489666986L;

	protected static final Logger log = LoggerFactory
			.getLogger(StringBinaryToIntegerExpression.class);

	private final Expression<String> left;
	private final Operator op;
	private final Expression<?> right;

	/**
	 * <p>
	 * Constructor for StringBinaryExpression.
	 * </p>
	 * 
	 * @param left2
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param op2
	 *            a {@link org.smartut.symbolic.expr.Operator} object.
	 * @param right2
	 *            a {@link org.smartut.symbolic.expr.Expression} object.
	 * @param con
	 *            a {@link java.lang.String} object.
	 */
	public StringBinaryToIntegerExpression(Expression<String> left2,
			Operator op2, Expression<?> right2, Long con) {
		super(con, 1 + left2.getSize() + right2.getSize(), left2
				.containsSymbolicVariable()
				|| right2.containsSymbolicVariable());
		this.left = left2;
		this.op = op2;
		this.right = right2;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH) {
			DSEStats.getInstance().reportConstraintTooLong(getSize());
			throw new ConstraintTooLongException(getSize());
		}
	}

	/** {@inheritDoc} */
	@Override
	public Operator getOperator() {
		return op;
	}

	/** {@inheritDoc} */
	@Override
	public Expression<String> getLeftOperand() {
		return left;
	}

	/** {@inheritDoc} */
	@Override
	public Expression<?> getRightOperand() {
		return right;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (op == Operator.INDEXOFC) {

			Long longObject = (Long) right.accept(new ExpressionEvaluator(),
					null);
			return "(" + left + op.toString() + "\'"
					+ Character.toChars(longObject.intValue())[0] + "\')";
		}
		return "(" + left + op.toString() + right + ")";
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof StringBinaryToIntegerExpression) {
			StringBinaryToIntegerExpression other = (StringBinaryToIntegerExpression) obj;
			return this.op.equals(other.op) && this.left.equals(other.left)
					&& this.right.equals(other.right);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.left.hashCode() + this.op.hashCode()
				+ this.right.hashCode();
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
		result.addAll(this.left.getConstants());
		result.addAll(this.right.getConstants());
		return result;
	}

	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
}
