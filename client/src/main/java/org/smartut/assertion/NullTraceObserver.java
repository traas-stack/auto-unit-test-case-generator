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

import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.ArrayStatement;
import org.smartut.testcase.statements.FunctionalMockStatement;
import org.smartut.testcase.statements.PrimitiveStatement;

public class NullTraceObserver extends AssertionTraceObserver<NullTraceEntry> {

	/** {@inheritDoc} */
	@Override
	public synchronized void afterStatement(Statement statement, Scope scope,
	        Throwable exception) {
		// By default, no assertions are created for statements that threw exceptions
		if(exception != null)
			return;

		// No assertions are created for mock statements
		if(statement instanceof FunctionalMockStatement)
			return;

		visitReturnValue(statement, scope);
	}

	/* (non-Javadoc)
	 * @see org.smartut.assertion.AssertionTraceObserver#visit(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, org.smartut.testcase.VariableReference)
	 */
	/** {@inheritDoc} */
	@Override
	protected void visit(Statement statement, Scope scope, VariableReference var) {
		logger.debug("Checking for null of " + var);
		try {
			if (var == null
			        || var.isPrimitive()
			        //|| var.isWrapperType() // TODO: Wrapper types might make sense but there were failing assertions...
			        || var.isEnum()
			        || currentTest.getStatement(var.getStPosition()) instanceof PrimitiveStatement
			        || currentTest.getStatement(var.getStPosition()).isAssignmentStatement())
				return;

			if(var.getType() != null && var.getType().equals(Void.class)){
				return; // do not generate assertion for Void type
			}

			// We don't need assertions on constant values
			if (statement instanceof PrimitiveStatement<?>)
				return;

			// We don't need assertions on array values
			if (statement instanceof ArrayStatement)
				return;

			Object object = var.getObject(scope);
			trace.addEntry(statement.getPosition(), var, new NullTraceEntry(var,
			        object == null));
		} catch (CodeUnderTestException e) {
			logger.debug("", e);
			//throw new UnsupportedOperationException();
		}
	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		// do nothing
	}
}