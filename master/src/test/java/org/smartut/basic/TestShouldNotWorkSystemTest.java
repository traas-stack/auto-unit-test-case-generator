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
package org.smartut.basic;


import static org.junit.Assert.*;

import java.util.List;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.result.TestGenerationResult;
import org.junit.Test;



public class TestShouldNotWorkSystemTest extends SystemTestBase {

	@Test(expected=IllegalArgumentException.class)
	public void testShouldNotWorkOnSmartUtPackage(){
		SmartUt smartut = new SmartUt();
		
		String targetClass = SystemTestBase.class.getCanonicalName();
		
		Properties.TARGET_CLASS = targetClass;
		
		String[] command = new String[]{				
				"-generateSuite",
				"-class",
				targetClass
		};

		
		smartut.parseCommandLine(command);
	}
	

	@Test
	public void testJavaPackageNotOnProjectCP(){
		SmartUt smartut = new SmartUt();
		
		String targetClass = java.util.TreeMap.class.getCanonicalName();
		
		Properties.TARGET_CLASS = targetClass;
		
		String[] command = new String[]{				
				"-generateSuite",
				"-class",
				targetClass
		};

		
		Object result = smartut.parseCommandLine(command);
		//List<TestGenerationResult> results = (List<TestGenerationResult>)result;
		List<List<TestGenerationResult>> results = (List<List<TestGenerationResult>>)result;
		assertEquals(1, results.size());
		//TestGenerationResult testResult = results.iterator().next();
		TestGenerationResult testResult = results.get(0).get(0);
		System.out.println(testResult.getErrorMessage());
		assertFalse(testResult.getErrorMessage().isEmpty());
		assertEquals(TestGenerationResult.Status.ERROR, testResult.getTestGenerationStatus());
		
	}
	
}
