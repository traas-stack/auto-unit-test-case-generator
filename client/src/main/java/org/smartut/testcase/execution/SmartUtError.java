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
package org.smartut.testcase.execution;

/**
 * This error can be used to signal an throwable from smartut code, below the
 * class under test. E.g. the class under tests is instrumented to call the
 * method smartut.something() which throws and error. If the error is of the
 * type SmartUtError the exception is thrown. If it is of any other type, the
 * exception is catched and it is assumed, that the exception was thrown by the
 * class under test
 * 
 * @author Sebastian Steenbuck
 */
public class SmartUtError extends Error {
	private static final long serialVersionUID = 454018150971425158L;

	/**
	 * <p>
	 * Constructor for SmartUtError.
	 * </p>
	 * 
	 * @param cause
	 *            a {@link java.lang.Throwable} object.
	 */
	public SmartUtError(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constructor for SmartUtError.
	 * </p>
	 * 
	 * @param msg
	 *            a {@link java.lang.String} object.
	 */
	public SmartUtError(String msg) {
		super(msg);
	}
}
