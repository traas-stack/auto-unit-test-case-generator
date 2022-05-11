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
package org.smartut.symbolic.expr.reader;

import java.util.HashSet;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.ConstraintTooLongException;
import org.smartut.symbolic.expr.AbstractExpression;
import org.smartut.symbolic.expr.ExpressionVisitor;
import org.smartut.symbolic.expr.Variable;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.str.StringValue;

public final class StringReaderExpr extends AbstractExpression<Long> implements
		IntegerValue {


	private static final long serialVersionUID = -744964586007203884L;

	private final StringValue string;
	private final int readerPosition;

	public StringReaderExpr(Long conc_value, StringValue string) {
		this(conc_value, string, 0);
	}

	public StringReaderExpr(Long conc_value, StringValue string, int readerPosition) {
		super(conc_value, 1 + string.getSize(), string.containsSymbolicVariable());

		this.string = string;
		this.readerPosition = readerPosition;

		if (getSize() > Properties.DSE_CONSTRAINT_LENGTH)
			throw new ConstraintTooLongException(getSize());
	}

	@Override
	public Set<Variable<?>> getVariables() {
        Set<Variable<?>> variables = new HashSet<>(this.string.getVariables());
		return variables;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this == obj)
			return true;

		if (obj instanceof StringReaderExpr) {
			StringReaderExpr that = (StringReaderExpr) obj;
			return this.string.equals(that.string)
					&& this.readerPosition == that.readerPosition;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return string.hashCode() + readerPosition;
	}

	@Override
	public String toString() {
		String toString = String.format("STRING_READER(%s, %s)",
				string.toString(), readerPosition);
		return toString;
	}

	public int getReaderPosition() {
		return readerPosition;
	}

	public StringValue getString() {
		return string;
	}

	@Override
	public Set<Object> getConstants() {
		Set<Object> result = new HashSet<>();
		result.add(string.getConcreteValue());
		return result;
	}
	
	@Override
	public <K, V> K accept(ExpressionVisitor<K, V> v, V arg) {
		return v.visit(this, arg);
	}
}
