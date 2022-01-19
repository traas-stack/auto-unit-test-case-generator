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
package org.smartut.testcarver.testcase;

import org.smartut.testcarver.capture.FieldRegistry;
import org.smartut.testcase.variable.FieldReference;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.AssignmentStatement;
import org.smartut.utils.generic.GenericField;
import org.objectweb.asm.Type;

public final class TestCarvingExecutionObserver extends ExecutionObserver {
	private int captureId;

	public TestCarvingExecutionObserver() {
		// We can't know the max captureId calculated in the test carving related 
		// instrumentation. However, we know the captureId starts with Integer.MIN_VALUE.
		// For this reason, we start with Integer.MAX_VALUE and decrement the captureId
		// to avoid id collisions 
		this.captureId = Integer.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void output(int position, String output) {
		// do nothing
	}

	/**
	 * own comment..
	 */
	@Override
	public void afterStatement(final Statement statement, final Scope scope,
	        final Throwable exception) {
		if (statement instanceof AssignmentStatement) {
			final AssignmentStatement assign = (AssignmentStatement) statement;
			final VariableReference left = assign.getReturnValue();

			if (left instanceof FieldReference) {
				final FieldReference fieldRef = (FieldReference) left;
				final GenericField field = fieldRef.getField();

				FieldRegistry.notifyModification(field.isStatic() ? null : scope.getObject(fieldRef.getSource()), this.captureId,
				                                 Type.getInternalName(field.getDeclaringClass()),
				                                 field.getName(),
				                                 Type.getDescriptor(field.getField().getType()));
				//PUTFIELDRegistry creates PUTXXX as well as corresponding GETXXX statements
				this.captureId -= 2;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.ExecutionObserver#beforeStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope)
	 */
	@Override
	public void beforeStatement(Statement statement, Scope scope) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		// do nothing
	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		// do nothing
	}

}
