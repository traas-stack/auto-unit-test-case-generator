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
package org.smartut.symbolic.expr.token;

import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.ConstraintTooLongException;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Variable;
import org.smartut.symbolic.expr.str.StringValue;

public final class NextTokenizerExpr extends TokenizerExpr {

	private final TokenizerExpr tokenizerExpr;

	public NextTokenizerExpr(TokenizerExpr expr) {
		super(1 + expr.getSize(), expr.containsSymbolicVariable());
		tokenizerExpr = expr;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH)
			throw new ConstraintTooLongException(getSize());
	}


	private static final long serialVersionUID = -5041244020293557448L;

	@Override
	public Set<Variable<?>> getVariables() {
		return tokenizerExpr.getVariables();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if (obj instanceof NextTokenizerExpr) {
			NextTokenizerExpr that = (NextTokenizerExpr)obj;
			return this.tokenizerExpr.equals(that.tokenizerExpr);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return tokenizerExpr.hashCode();
	}

	@Override
	public String toString() {
		return "NEXT_TOKEN(" + tokenizerExpr.toString() + ")";
	}

	@Override
	public StringValue getDelimiter() {
		return tokenizerExpr.getDelimiter();
	}

	@Override
	public StringValue getString() {
		return tokenizerExpr.getString();
	}

	@Override
	public int getNextTokenCount() {
		return 1 + tokenizerExpr.getNextTokenCount();
	}

	@Override
	public Set<Object> getConstants() {
		return tokenizerExpr.getConstants();
	}
	
	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
	
	public TokenizerExpr getTokenizerExpr() {
		return tokenizerExpr;
	}
}
