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
package org.smartut.junit.rules;

import java.util.Arrays;

import org.smartut.TestGenerationContext;

/**
 * Should be used as MethodRule
 */
public class StaticStateResetter extends BaseRule {

	private String[] classNames;
	
	public StaticStateResetter(String... classesToReset) {
		classNames = Arrays.copyOf(classesToReset, classesToReset.length);
		org.smartut.Properties.RESET_STATIC_FIELDS = true;
		
		/*
		 * FIXME: tmp hack done during refactoring
		 */
		org.smartut.runtime.classhandling.ClassResetter.getInstance().setClassLoader(
				TestGenerationContext.getInstance().getClassLoaderForSUT());
	}
	
	@Override
	protected void before() {
	}

	@Override
	protected void after() {
		for (String classNameToReset : classNames) {
			try {
				org.smartut.runtime.classhandling.ClassResetter.getInstance().reset(classNameToReset);
			} catch (Throwable t) {
			}
		}
	}
}
