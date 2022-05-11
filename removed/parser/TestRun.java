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
package org.smartut.junit;

import org.smartut.testcase.ExecutionTrace;
public class TestRun {
	private final ExecutionTrace executionTrace;
	private final Throwable failure;

	/**
	 * <p>Constructor for TestRun.</p>
	 *
	 * @param executionTrace a {@link org.smartut.testcase.ExecutionTrace} object.
	 * @param failure a {@link java.lang.Throwable} object.
	 */
	public TestRun(ExecutionTrace executionTrace, Throwable failure) {
		this.executionTrace = executionTrace;
		this.failure = failure;
	}

	/**
	 * <p>Getter for the field <code>executionTrace</code>.</p>
	 *
	 * @return a {@link org.smartut.testcase.ExecutionTrace} object.
	 */
	public ExecutionTrace getExecutionTrace() {
		return executionTrace;
	}

	/**
	 * <p>Getter for the field <code>failure</code>.</p>
	 *
	 * @return a {@link java.lang.Throwable} object.
	 */
	public Throwable getFailure() {
		return failure;
	}
}
