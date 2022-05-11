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
package org.smartut.assertion;

import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.Scope;
public class NullAssertion extends Assertion {

	private static final long serialVersionUID = 8486987896764253928L;

	/** {@inheritDoc} */
	@Override
	public Assertion copy(TestCase newTestCase, int offset) {
		NullAssertion s = new NullAssertion();
		s.source = newTestCase.getStatement(source.getStPosition() + offset).getReturnValue();
		s.value = value;
		s.comment = comment;
		s.killedMutants.addAll(killedMutants);
		assert (s.isValid());
		return s;
	}

	/** {@inheritDoc} */
	@Override
	public boolean evaluate(Scope scope) {
		try {
			if ((Boolean) value) {
				return source.getObject(scope) == null;
			} else {
				return source.getObject(scope) != null;
			}
		} catch (CodeUnderTestException e) {
			throw new UnsupportedOperationException();
		}

	}

	/** {@inheritDoc} */
	@Override
	public String getCode() {
		if ((Boolean) value) {
			return "assertNull(" + source.getName() + ");";
		} else
			return "assertNotNull(" + source.getName() + ");";
	}

}
