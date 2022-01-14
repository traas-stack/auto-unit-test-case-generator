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

import java.util.Set;

import org.smartut.testcase.statements.*;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testcase.execution.Scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Abstract AssertionTraceObserver class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public abstract class AssertionTraceObserver<T extends OutputTraceEntry> extends
        ExecutionObserver {

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(AssertionTraceObserver.class);

	protected OutputTrace<T> trace = new OutputTrace<>();

	protected boolean checkThread() {
		return ExecutionTracer.isThreadNeqCurrentThread();
	}
	
	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#output(int, java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public synchronized void output(int position, String output) {
		// Default behavior is to ignore console output

	}

	/**
	 * <p>
	 * visitDependencies
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 */
	protected void visitDependencies(Statement statement, Scope scope) {
		Set<VariableReference> dependencies = currentTest.getDependencies(statement.getReturnValue());

		for (VariableReference var : dependencies) {
			if(var.isVoid())
				continue;
			// No assertions on mocked objects
			if(statement.getTestCase().getStatement(var.getStPosition()) instanceof FunctionalMockStatement)
				continue;
			if (!var.isVoid()) {
				try {
					visit(statement, scope, var);
				} catch (CodeUnderTestException e) {
					// ignore
				}
			}
		}
	}
	

	/**
	 * <p>
	 * visitReturnValue
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 */
	protected void visitReturnValue(Statement statement, Scope scope) {
		if (statement.getReturnClass().equals(void.class))
			return;
		
		// No need to assert anything about values just assigned
		if(statement.isAssignmentStatement())
			return;

		try {
			visit(statement, scope, statement.getReturnValue());
		} catch (CodeUnderTestException e) {
			// ignore
		}

	}

	/**
	 * <p>
	 * visit
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 * @param scope
	 *            a {@link org.smartut.testcase.execution.Scope} object.
	 * @param var
	 *            a {@link org.smartut.testcase.variable.VariableReference} object.
	 */
	protected abstract void visit(Statement statement, Scope scope,
	        VariableReference var) throws CodeUnderTestException;

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#statement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public synchronized void afterStatement(Statement statement, Scope scope,
	        Throwable exception) {
		//if(checkThread())
		//	return;
		
		// No assertions are created for mock statements
		if(statement instanceof FunctionalMockStatement)
			return;

        // No assertions for primitives
        if(statement instanceof PrimitiveStatement<?>)
            return;


        // By default, no assertions are created for statements that threw exceptions
		if(exception != null)
			return;

		if(statement instanceof FieldStatement) {
			// Only need to check returnvalue here, nothing else can have changed
			visitReturnValue(statement, scope);
		}
		else {
			visitDependencies(statement, scope);
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#beforeStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope)
	 */
	@Override
	public synchronized void beforeStatement(Statement statement, Scope scope) {
		// Do nothing
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#clear()
	 */
	/** {@inheritDoc} */
	@Override
	public synchronized void clear() {
		//if(!checkThread())
		//	return;

		trace.clear();
	}

	/**
	 * <p>
	 * Getter for the field <code>trace</code>.
	 * </p>
	 * 
	 * @return a {@link org.smartut.assertion.OutputTrace} object.
	 */
	public synchronized OutputTrace<T> getTrace() {
		return trace.clone();
	}

}
