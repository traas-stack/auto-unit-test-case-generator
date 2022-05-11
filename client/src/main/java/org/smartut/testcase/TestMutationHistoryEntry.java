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
package org.smartut.testcase;

import java.io.Serializable;

import org.smartut.ga.operators.mutation.MutationHistoryEntry;
import org.smartut.testcase.statements.Statement;

public class TestMutationHistoryEntry implements MutationHistoryEntry, Serializable {

	private static final long serialVersionUID = -4278409687247714553L;

	public enum TestMutation {
		CHANGE, INSERTION, DELETION
	};

	protected TestMutation mutationType;

	protected Statement statement;

	public String whatwasit;

	public TestMutationHistoryEntry(TestMutation type, Statement statement) {
		this.mutationType = type;
		this.statement = statement;
		this.whatwasit = statement.getCode() + " at position " + statement.getPosition();
	}

	public TestMutationHistoryEntry(TestMutation type) {
		this.mutationType = type;
		this.statement = null;
		this.whatwasit = "Deleted some statement";
	}

	public Statement getStatement() {
		return statement;
	}

	public TestMutation getMutationType() {
		return mutationType;
	}

	public TestMutationHistoryEntry clone(TestCase newTest) {
		if (statement == null)
			return new TestMutationHistoryEntry(mutationType);

		return new TestMutationHistoryEntry(mutationType,
		        newTest.getStatement(statement.getPosition()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mutationType + " at " + statement;
	}
}
