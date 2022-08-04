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

import org.smartut.Properties;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.AssignmentStatement;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.objectweb.asm.Type;


public class ComparisonTraceObserver extends AssertionTraceObserver<ComparisonTraceEntry> {

	/* (non-Javadoc)
	 * @see org.smartut.assertion.AssertionTraceObserver#visit(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, org.smartut.testcase.VariableReference)
	 */
	/** {@inheritDoc} */
	@Override
	protected void visit(Statement statement, Scope scope, VariableReference var) {
		try {
			Object object = var.getObject(scope);
			if (object == null)
				return;

			if(statement instanceof AssignmentStatement)
				return;
            if(statement instanceof PrimitiveStatement<?>)
                return;

			ComparisonTraceEntry entry = new ComparisonTraceEntry(var);
			int position = statement.getPosition();

			for (VariableReference other : scope.getElements(var.getType())) {
				Object otherObject = other.getObject(scope);
				// TODO: Create a matrix of object comparisons?
				if (otherObject == null)
					continue; // TODO: Don't do this?

				if (object == otherObject)
					continue; // Don't compare with self?

                int otherPos = other.getStPosition();
                if(otherPos >= position)
                    continue; // Don't compare with variables that are not defined - may happen with primitives?

				Statement otherStatement = currentTest.getStatement(otherPos);

				if (statement instanceof PrimitiveStatement && otherStatement instanceof PrimitiveStatement)
					continue; // Don't compare two primitives

				if(otherStatement instanceof MethodStatement) {
					if(((MethodStatement)otherStatement).getMethodName().equals("hashCode"))
						continue; // No comparison against hashCode, as the hashCode return value will not be in the test
				}


				if (Properties.PURE_EQUALS) {
					String className = object.getClass().getCanonicalName();
					String methodName = "equals";
					String descriptor = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Object.class));
					CheapPurityAnalyzer cheapPurityAnalyzer = CheapPurityAnalyzer.getInstance();
					if (!cheapPurityAnalyzer.isPure(className, methodName, descriptor))
						continue; //Don't compare using impure equals(Object) methods		
				}
				
				try {
					logger.debug("Comparison of " + var + " with " + other + " is: "
					        + object.equals(otherObject));
					entry.addEntry(other, ComparisonTraceEntry.equals(object, otherObject));
				} catch (Throwable t) {
					logger.debug("Exception during equals: " + t);
					// ignore?
				}
				if (object instanceof Comparable<?>) {
					// TODO
				}
			}

			trace.addEntry(statement.getPosition(), var, entry);
		} catch (CodeUnderTestException e) {
			logger.debug("", e);
		}

	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		// do nothing
	}
}