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
package org.smartut.clinit;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collections;

import org.smartut.Properties;
import org.smartut.TestGenerationContext;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.execution.SmartUtError;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.statements.StringPrimitiveStatement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.generic.GenericMethod;
import org.junit.Test;

import com.examples.with.different.packagename.clinit.SimpleClass;

public class TestClassInitialization {

	private static DefaultTestCase buildLoadTargetClassTestCase(String className) throws SmartUtError {
		DefaultTestCase test = new DefaultTestCase();

		StringPrimitiveStatement stmt0 = new StringPrimitiveStatement(test, className);
		VariableReference string0 = test.addStatement(stmt0);
		try {
			Method currentThreadMethod = Thread.class.getMethod("currentThread");
			Statement currentThreadStmt = new MethodStatement(test,
					new GenericMethod(currentThreadMethod, currentThreadMethod.getDeclaringClass()), null,
					Collections.emptyList());
			VariableReference currentThreadVar = test.addStatement(currentThreadStmt);

			Method getContextClassLoaderMethod = Thread.class.getMethod("getContextClassLoader");
			Statement getContextClassLoaderStmt = new MethodStatement(test,
					new GenericMethod(getContextClassLoaderMethod, getContextClassLoaderMethod.getDeclaringClass()),
					currentThreadVar, Collections.emptyList());
			VariableReference contextClassLoaderVar = test.addStatement(getContextClassLoaderStmt);

			Method loadClassMethod = ClassLoader.class.getMethod("loadClass", String.class);
			Statement loadClassStmt = new MethodStatement(test,
					new GenericMethod(loadClassMethod, loadClassMethod.getDeclaringClass()), contextClassLoaderVar,
					Collections.singletonList(string0));
			test.addStatement(loadClassStmt);

			return test;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new SmartUtError("Unexpected exception while creating Class Initializer Test Case");
		}
	}

	@Test
	public void checksClassIsLoadedUsingInstrumentingClassLoader() throws ClassNotFoundException {
		Properties.CLIENT_ON_THREAD = true;
		final String className = SimpleClass.class.getCanonicalName();
		TestCaseExecutor.initExecutor();
		TestGenerationContext.getInstance().resetContext();
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		assertFalse(classLoader.getLoadedClasses().contains(className));
		DefaultTestCase test = buildLoadTargetClassTestCase(className);
		TestCaseExecutor.getInstance().execute(test, Integer.MAX_VALUE);
		assertTrue(classLoader.getLoadedClasses().contains(className));
	}

}
