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
package org.smartut.runtime.mock.java.util.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.smartut.runtime.mock.OverrideMock;

public class MockLogRecord extends LogRecord  implements OverrideMock{

	public MockLogRecord(Level level, String msg) {
		super(level, msg);
		setMillis(org.smartut.runtime.System.currentTimeMillis());
		setSequenceNumber(0L);
		setThreadID(0);
	}

	private static final long serialVersionUID = -1511890873640420434L;

}
