package org.smartut.assertion;

import org.smartut.testcase.variable.VariableReference;

import java.util.HashSet;
import java.util.Set;

public class ArrayLengthTraceEntry implements OutputTraceEntry {

    protected VariableReference var;

    protected int length;

    public ArrayLengthTraceEntry(VariableReference var, Object[] value) {
        this.var = var;
        this.length = value.length;
    }

    public ArrayLengthTraceEntry(VariableReference var, int length) {
        this.var = var;
        this.length = length;
    }

    /* (non-Javadoc)
     * @see org.smartut.assertion.OutputTraceEntry#differs(org.smartut.assertion.OutputTraceEntry)
     */
    @Override
    public boolean differs(OutputTraceEntry other) {
        if (other instanceof ArrayLengthTraceEntry) {
            ArrayLengthTraceEntry otherEntry = (ArrayLengthTraceEntry) other;
            if (length !=  otherEntry.length)
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
        if (other instanceof ArrayLengthTraceEntry) {
            ArrayLengthTraceEntry otherEntry = (ArrayLengthTraceEntry) other;
            if (length != otherEntry.length) {
                ArrayLengthAssertion assertion = new ArrayLengthAssertion();
                assertion.length = length;
                assertion.source = var;
                assertion.value = length;
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
        ArrayLengthAssertion assertion = new ArrayLengthAssertion();
        assertion.source = var;
        assertion.length = length;
        assertion.value = length;
        assertions.add(assertion);
        assert (assertion.isValid());

        return assertions;
    }

    /* (non-Javadoc)
     * @see org.smartut.assertion.OutputTraceEntry#isDetectedBy(org.smartut.assertion.Assertion)
     */
    @Override
    public boolean isDetectedBy(Assertion assertion) {
        if (assertion instanceof ArrayLengthAssertion) {
            ArrayLengthAssertion ass = (ArrayLengthAssertion) assertion;
            if (var.equals(ass.source)) {
                if (length != ass.length) {
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
        return new ArrayLengthTraceEntry(var, length);
    }

}
