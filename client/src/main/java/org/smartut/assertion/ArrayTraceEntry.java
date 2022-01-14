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

package org.smartut.assertion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.smartut.testcase.variable.VariableReference;

/**
 * @author Gordon Fraser
 * 
 */
public class ArrayTraceEntry implements OutputTraceEntry {

	protected VariableReference var;

	protected Object[] value;

	public ArrayTraceEntry(VariableReference var, Object[] value) {
		this.var = var;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#differs(org.smartut.assertion.OutputTraceEntry)
	 */
	@Override
	public boolean differs(OutputTraceEntry other) {
		if (other instanceof ArrayTraceEntry) {
			ArrayTraceEntry otherEntry = (ArrayTraceEntry) other;
			if (!Arrays.equals(value, otherEntry.value))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#getAssertions(org.smartut.assertion.OutputTraceEntry)
	 */
	@Override
	public Set<Assertion> getAssertions(OutputTraceEntry other) {
		Set<Assertion> assertions = new HashSet<>();
		if (other instanceof ArrayTraceEntry) {
			ArrayTraceEntry otherEntry = (ArrayTraceEntry) other;
			if (!Arrays.equals(value, otherEntry.value)) {
				ArrayEqualsAssertion assertion = new ArrayEqualsAssertion();
				assertion.value = value;
				assertion.source = var;
				assertions.add(assertion);
				assert (assertion.isValid());
			}
		}
		return assertions;
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#getAssertions()
	 */
	@Override
	public Set<Assertion> getAssertions() {
		Set<Assertion> assertions = new HashSet<>();
		ArrayEqualsAssertion assertion = new ArrayEqualsAssertion();
		assertion.source = var;
		assertion.value = value;
		assertions.add(assertion);
		assert (assertion.isValid());

		return assertions;
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#isDetectedBy(org.smartut.assertion.Assertion)
	 */
	@Override
	public boolean isDetectedBy(Assertion assertion) {
		if (assertion instanceof ArrayEqualsAssertion) {
			ArrayEqualsAssertion ass = (ArrayEqualsAssertion) assertion;
			if (var.equals(ass.source)) {
				if (!Arrays.equals(value, (Object[]) ass.value)) {
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#cloneEntry()
	 */
	@Override
	public OutputTraceEntry cloneEntry() {
		return new ArrayTraceEntry(var, Arrays.copyOf(value, value.length));
	}

}
