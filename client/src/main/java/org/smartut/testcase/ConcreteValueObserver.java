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

package org.smartut.testcase;

import java.util.HashMap;
import java.util.Map;

import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;

/**
 * @author Gordon Fraser
 * 
 */
public class ConcreteValueObserver extends ExecutionObserver {

	private final Map<Integer, Object> concreteValues = new HashMap<>();

	public Map<Integer, Object> getConcreteValues() {
		return concreteValues;
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#output(int, java.lang.String)
	 */
	@Override
	public void output(int position, String output) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#beforeStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope)
	 */
	@Override
	public void beforeStatement(Statement statement, Scope scope) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#afterStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, java.lang.Throwable)
	 */
	@Override
	public void afterStatement(Statement statement, Scope scope,
	        Throwable exception) {
		int numStatement = statement.getPosition();
		VariableReference returnValue = statement.getReturnValue();
		if (!returnValue.isPrimitive()) {
			// Only interested in primitive values
			return;
		}
		TestCase test = super.getCurrentTest();
		if (test.getStatement(returnValue.getStPosition()) instanceof PrimitiveStatement<?>) {
			// Don't need to collect primitive statement values
			return;
		}
		try {
			Object object = statement.getReturnValue().getObject(scope);
			concreteValues.put(numStatement, object);
		} catch(CodeUnderTestException e) {
			// Ignore
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#clear()
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		// do nothing
	}
}
