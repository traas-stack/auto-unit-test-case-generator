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
package org.smartut.symbolic.expr.str;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IntegerToStringCast extends AbstractExpression<String> implements
        StringValue, Cast<Long> {

	private static final long serialVersionUID = 2414222998301630838L;

	protected static Logger log = LoggerFactory.getLogger(IntegerToStringCast.class);

	private final Expression<Long> expr;

	public IntegerToStringCast(Expression<Long> expr, String concV) {
		super(concV, 1 + expr.getSize(), expr.containsSymbolicVariable());
		this.expr = expr;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH) {
			DSEStats.getInstance().reportConstraintTooLong(getSize());
			throw new ConstraintTooLongException(getSize());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "((String)" + expr + ")";
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof IntegerToStringCast) {
			IntegerToStringCast other = (IntegerToStringCast) obj;
			return this.expr.equals(other.expr);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return expr.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public Expression<Long> getArgument() {
		return expr;
	}

	@Override
	public Set<Variable<?>> getVariables() {
        Set<Variable<?>> variables = new HashSet<>(this.expr.getVariables());
		return variables;
	}

	@Override
	public Set<Object> getConstants() {
		return this.expr.getConstants();
	}
	
	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}

}
