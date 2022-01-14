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

import org.smartut.testcase.statements.*;


/**
 * <p>
 * TestVisitor interface.
 * </p>
 * 
 * @author fraser
 */
public abstract class TestVisitor {

	/**
	 * <p>
	 * visitTestCase
	 * </p>
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestCase} object.
	 */
	public abstract void visitTestCase(TestCase test);

	/**
	 * <p>
	 * visitPrimitiveStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.PrimitiveStatement} object.
	 */
	public abstract void visitPrimitiveStatement(PrimitiveStatement<?> statement);

	/**
	 * <p>
	 * visitFieldStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.FieldStatement} object.
	 */
	public abstract void visitFieldStatement(FieldStatement statement);

	/**
	 * <p>
	 * visitMethodStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.MethodStatement} object.
	 */
	public abstract void visitMethodStatement(MethodStatement statement);

	/**
	 * <p>
	 * visitConstructorStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.ConstructorStatement} object.
	 */
	public abstract void visitConstructorStatement(ConstructorStatement statement);

	/**
	 * <p>
	 * visitArrayStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.ArrayStatement} object.
	 */
	public abstract void visitArrayStatement(ArrayStatement statement);

	/**
	 * <p>
	 * visitAssignmentStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.AssignmentStatement} object.
	 */
	public abstract void visitAssignmentStatement(AssignmentStatement statement);

	/**
	 * <p>
	 * visitNullStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.NullStatement} object.
	 */
	public abstract void visitNullStatement(NullStatement statement);

	/**
	 * <p>
	 * visitPrimitiveExpression
	 * </p>
	 * 
	 * @param primitiveExpression
	 *            a {@link org.smartut.testcase.statements.PrimitiveExpression} object.
	 */
	public abstract void visitPrimitiveExpression(PrimitiveExpression primitiveExpression);


	public abstract void visitFunctionalMockStatement(FunctionalMockStatement functionalMockStatement);

	/**
	 * <p>
	 * visitStatement
	 * </p>
	 * 
	 * @param statement
	 *            a {@link org.smartut.testcase.statements.Statement} object.
	 */
	public void visitStatement(Statement statement) {

		if (statement instanceof PrimitiveStatement<?>)
			visitPrimitiveStatement((PrimitiveStatement<?>) statement);
		else if (statement instanceof FieldStatement)
			visitFieldStatement((FieldStatement) statement);
		else if (statement instanceof ConstructorStatement)
			visitConstructorStatement((ConstructorStatement) statement);
		else if (statement instanceof MethodStatement)
			visitMethodStatement((MethodStatement) statement);
		else if (statement instanceof AssignmentStatement)
			visitAssignmentStatement((AssignmentStatement) statement);
		else if (statement instanceof ArrayStatement)
			visitArrayStatement((ArrayStatement) statement);
		else if (statement instanceof NullStatement)
			visitNullStatement((NullStatement) statement);
		else if (statement instanceof PrimitiveExpression)
			visitPrimitiveExpression((PrimitiveExpression) statement);
		else if (statement instanceof FunctionalMockStatement)
			visitFunctionalMockStatement((FunctionalMockStatement) statement);
		else
			throw new RuntimeException("Unknown statement type: " + statement);
	}
}
