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

package org.smartut.contracts;

import java.util.Arrays;
import java.util.List;

import org.smartut.PackageInfo;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.MethodStatement;


/**
 * <p>JCrasherExceptionContract class.</p>
 *
 * @author fraser
 */
public class JCrasherExceptionContract extends Contract {

	// ArrayIndexOutOfBoundsException, NegativeArraySizeException, ArrayStoreException, ClassCastException, and ArithmeticException

	private final static Class<?>[] uncheckedBugExceptions = {
	        ArrayIndexOutOfBoundsException.class, NegativeArraySizeException.class,
	        ArrayStoreException.class, ClassCastException.class,
	        ArithmeticException.class };

	private final static List<Class<?>> uncheckedExceptions = Arrays.asList(uncheckedBugExceptions);

	/* (non-Javadoc)
	 * @see org.smartut.contracts.Contract#check(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public ContractViolation check(Statement statement, Scope scope, Throwable exception) {
		if (!isTargetStatement(statement))
			return null;

		if (exception != null) {
			if (exception instanceof CodeUnderTestException)
				return null;
			if (exception instanceof RuntimeException) {
				if (uncheckedExceptions.contains(exception.getClass()))
					return new ContractViolation(this, statement, exception);
				else {
					StackTraceElement element = exception.getStackTrace()[0];

					String methodName = "";
					if (statement instanceof ConstructorStatement)
						methodName = "<init>";
					else if (statement instanceof MethodStatement)
						methodName = ((MethodStatement) statement).getMethod().getName();
					else
						return null;

					// If the exception was thrown in the called method we assume it is a bug in the test, not the class		      
					if (element.getMethodName().equals(methodName)) {
						return null;
					}
					// If the exception was thrown in the test directly, it is also not interesting
					if (element.getClassName().startsWith(PackageInfo.getSmartUtPackage()+".testcase")) {
						return null;
					}
					return new ContractViolation(this, statement, exception);
				}
			}
		}

		return null;
	}

	@Override
	public void addAssertionAndComments(Statement statement,
			List<VariableReference> variables, Throwable exception) {
		statement.addComment("Throws undeclared exception (JCrasher heuristic): " +exception.getMessage());
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Undeclared exception (JCrasher style)";
	}

}
