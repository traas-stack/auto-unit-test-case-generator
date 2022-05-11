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
package org.smartut.coverage.io.output;

import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.VariableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Jose Miguel Rojas
 */
public class OutputObserver extends ExecutionObserver {

    private Map<Integer, Set<OutputCoverageGoal>> outputCoverage = new LinkedHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(OutputObserver.class);

    /* (non-Javadoc)
     * @see org.smartut.testcase.ExecutionObserver#output(int, java.lang.String)
     */
    @Override
    public void output(int position, String output) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see org.smartut.testcase.ExecutionObserver#beforeStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope)
     */
    @Override
    public void beforeStatement(Statement statement, Scope scope) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see org.smartut.testcase.ExecutionObserver#afterStatement(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, java.lang.Throwable)
     */
    @Override
    public void afterStatement(Statement statement, Scope scope,
                               Throwable exception) {
        if (statement instanceof MethodStatement) {
            MethodStatement methodStmt = (MethodStatement) statement;
            VariableReference varRef = methodStmt.getReturnValue();

	        try {
		        Object returnObject = varRef.getObject(scope);
		        if (exception == null && !methodStmt.getReturnType().equals(Void.TYPE)) {
			        // we don't save anything if there was an exception
			        // we are only interested in methods whose return type != void

			        String className  = methodStmt.getDeclaringClassName();
			        String methodDesc = methodStmt.getDescriptor();
			        String methodName = methodStmt.getMethodName();

			        outputCoverage.put(statement.getPosition(), OutputCoverageGoal.createGoalsFromObject(className, methodName, methodDesc, returnObject));
		        }
	        } catch (CodeUnderTestException e) {
		        // ignore?
	        }
        }
    }

    /* (non-Javadoc)
     * @see org.smartut.testcase.ExecutionObserver#testExecutionFinished(org.smartut.testcase.ExecutionResult)
     */
    @Override
    public void testExecutionFinished(ExecutionResult r, Scope s) {
        logger.debug("Adding returnValues map to ExecutionResult");
        r.setOutputGoals(outputCoverage);
    }

    /* (non-Javadoc)
     * @see org.smartut.testcase.ExecutionObserver#clear()
     */
    @Override
    public void clear() {
        outputCoverage = new LinkedHashMap<>();
    }

}
