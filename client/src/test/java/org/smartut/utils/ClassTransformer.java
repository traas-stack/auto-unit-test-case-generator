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
package org.smartut.utils;

import java.util.HashMap;
import java.util.Map;

import org.smartut.Properties;
import org.smartut.instrumentation.testability.TestabilityTransformationClassLoader;
import org.smartut.testcase.execution.ExecutionTracer;


public class ClassTransformer {

	private static ClassTransformer instance = new ClassTransformer();

	public static ClassTransformer getInstance() {
		return instance;
	}

	private final Map<String, Class<?>> instrumentedClasses = new HashMap<>();

	private ClassTransformer() {
		// private constructor
	}

	public Class<?> instrumentClass(String fullyQualifiedTargetClass) {
		Class<?> result = instrumentedClasses.get(fullyQualifiedTargetClass);
		if (result != null) {
			assert Properties.TARGET_CLASS.equals(fullyQualifiedTargetClass);
			assert Properties.PROJECT_PREFIX.equals(fullyQualifiedTargetClass);
			return result;
		}
		try {
			Properties.TARGET_CLASS = fullyQualifiedTargetClass;
			Properties.PROJECT_PREFIX = fullyQualifiedTargetClass;
			ExecutionTracer.enable();
			ClassLoader classLoader = new TestabilityTransformationClassLoader();
			result = classLoader.loadClass(fullyQualifiedTargetClass);
			instrumentedClasses.put(fullyQualifiedTargetClass, result);
			return result;
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc);
		}
	}
}
