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
package org.smartut.runtime.classhandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.junit.Test;

import com.examples.with.different.packagename.reset.StaticInitThrowsNullPointer;

public class StaticInitThrowsNullPointerSystemTest extends SystemTestBase {

	/*
	 * These tests are based on issues found on project 44_summa, which is using the lucene API.
	 * those have issues when for example classes uses org.apache.lucene.util.Constants which has:
	 * 
	  try {
	    Collections.class.getMethod("emptySortedSet");
	  } catch (NoSuchMethodException nsme) {
	    v8 = false;
	  }
	  *
	  * in its static initializer
	 */

	@Test
	public void testWithNoReset() {
		Properties.RESET_STATIC_FIELDS = false;

		SmartUt smartut = new SmartUt();

		String targetClass = StaticInitThrowsNullPointer.class
				.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);
		assertTrue(result instanceof List);
		List<?> list = (List<?>)result;
		assertEquals(0, list.size());
	}

	@Test
	public void testWithReset() {
		Properties.RESET_STATIC_FIELDS = true;

		SmartUt smartut = new SmartUt();

		String targetClass = StaticInitThrowsNullPointer.class
				.getCanonicalName();

		Properties.TARGET_CLASS = targetClass;
		String[] command = new String[] { "-generateSuite", "-class",
				targetClass };

		Object result = smartut.parseCommandLine(command);
		assertTrue(result instanceof List);
		List<?> list = (List<?>)result;
		assertEquals(0, list.size());
	}
}
