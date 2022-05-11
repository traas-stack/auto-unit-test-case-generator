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
package org.smartut.runtime;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.CallExit;

import java.lang.*;
import java.security.Permission;

import static org.junit.Assert.assertFalse;

public class SUTWithSystemExitSystemTest extends SystemTestBase {

	@Before
	public void setFlag(){
		SafeExit.calledExit = false;
	}

	@Test
	public void testSystemExit_noAssertions() {
		Properties.ASSERTIONS = false;
		testSystemExit();
	}

		@Test
	public void testSystemExit() {

		java.lang.System.setSecurityManager(new SafeExit());

		SmartUt smartut = new SmartUt();

		String targetClass = CallExit.class.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		Properties.REPLACE_CALLS = true;

		String[] command = new String[] { "-generateSuite", "-class", targetClass };

		smartut.parseCommandLine(command);

		assertFalse(SafeExit.calledExit);
	}

	@After
	public void removeSecurity(){
		java.lang.System.setSecurityManager(null);
	}

	private static class SafeExit extends SecurityManager{

		public static boolean calledExit = false;

		public void checkPermission(Permission perm) throws SecurityException {

			final String name = perm.getName().trim();
			if (name.startsWith("exitVM")){
				calledExit = true;
				throw new RuntimeException("CALLED EXIT");
			}
		}
	}

}
