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

import java.util.HashSet;
import java.util.Set;
import org.smartut.testcase.variable.VariableReference;


/**
 * <p>PrimitiveTraceEntry class.</p>
 *
 * @author Gordon Fraser
 */
public class PrimitiveTraceEntry implements OutputTraceEntry {

  protected VariableReference var;

  protected Object value;

  /**
   * <p>Constructor for PrimitiveTraceEntry.</p>
   *
   * @param var a {@link org.smartut.testcase.variable.VariableReference} object.
   * @param value a {@link java.lang.Object} object.
   */
  public PrimitiveTraceEntry(VariableReference var, Object value) {
    this.var = var;
    this.value = value;
  }

	/* (non-Javadoc)
   * @see org.smartut.assertion.OutputTraceEntry#differs(org.smartut.assertion.OutputTraceEntry)
	 */

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean differs(OutputTraceEntry other) {
    if (other instanceof PrimitiveTraceEntry) {
      PrimitiveTraceEntry otherEntry = (PrimitiveTraceEntry) other;
      if (!value.equals(otherEntry.value)) {
        return true;
      }
    }
    return false;
  }

	/* (non-Javadoc)
   * @see org.smartut.assertion.OutputTraceEntry#getAssertion(org.smartut.assertion.OutputTraceEntry)
	 */

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Assertion> getAssertions(OutputTraceEntry other) {
    Set<Assertion> assertions = new HashSet<>();
    if (other instanceof PrimitiveTraceEntry) {
      PrimitiveTraceEntry otherEntry = (PrimitiveTraceEntry) other;
      if (otherEntry != null && otherEntry.value != null && value != null
          && var.getStPosition() == otherEntry.var.getStPosition()) {
        if (!value.equals(otherEntry.value)) {
          PrimitiveAssertion assertion = new PrimitiveAssertion();
          assertion.value = value;
          assertion.source = var;
          assertions.add(assertion);
          assert (assertion.isValid());
        }
      }
    }
    return assertions;
  }

	/* (non-Javadoc)
   * @see org.smartut.assertion.OutputTraceEntry#getAssertion()
	 */

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Assertion> getAssertions() {
    Set<Assertion> assertions = new HashSet<>();
    PrimitiveAssertion assertion = new PrimitiveAssertion();
    assertion.source = var;
    assertion.value = value;
    assertions.add(assertion);
    assert (assertion.isValid());

    return assertions;
  }

	/* (non-Javadoc)
   * @see org.smartut.assertion.OutputTraceEntry#isDetectedBy(org.smartut.assertion.Assertion)
	 */

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDetectedBy(Assertion assertion) {
    if (assertion instanceof PrimitiveAssertion) {
      PrimitiveAssertion ass = (PrimitiveAssertion) assertion;
      if (var.same(ass.source)) {
        return !value.equals(ass.value);
      }
    }
    return false;
  }

	/* (non-Javadoc)
	 * @see org.smartut.assertion.OutputTraceEntry#cloneEntry()
	 */

  /**
   * {@inheritDoc}
   */
  @Override
  public OutputTraceEntry cloneEntry() {
    return new PrimitiveTraceEntry(var, value);
  }

}
