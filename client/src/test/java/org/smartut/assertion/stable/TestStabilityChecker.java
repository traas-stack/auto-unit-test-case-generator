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
package org.smartut.assertion.stable;

import java.util.List;

import org.smartut.Properties;
import org.smartut.junit.JUnitAnalyzer;
import org.smartut.runtime.sandbox.Sandbox;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.TestCaseExecutor;

public abstract class TestStabilityChecker {
	public static boolean checkStability(List<TestCase> list) {
		int n = list.size();
		boolean previousRunOnSeparateProcess = Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS;
		Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = false;
		
		TestCaseExecutor.initExecutor(); //needed because it gets pulled down after the search

		//Before starting search, let's activate the sandbox
		if (Properties.SANDBOX) {
			Sandbox.initializeSecurityManagerForSUT();
		}

		
		for (TestCase tc : list) {
			if (tc.isUnstable()) {
				return false;
			}
		}



		try {
			JUnitAnalyzer.removeTestsThatDoNotCompile(list);
			if (n != list.size()) {
				return false;
			}

			JUnitAnalyzer.handleTestsThatAreUnstable(list);
			if (n != list.size()) {
				return false;
			}

			for (TestCase tc : list) {
				if (tc.isUnstable()) {
					return false;
				}
			}

			return true;
		} finally {
			Properties.JUNIT_CHECK_ON_SEPARATE_PROCESS = previousRunOnSeparateProcess;
			if (Properties.SANDBOX) {
				Sandbox.resetDefaultSecurityManager();
			}
		}
	}

}
