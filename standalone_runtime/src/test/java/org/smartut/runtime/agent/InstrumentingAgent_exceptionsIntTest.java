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
package org.smartut.runtime.agent;

import org.smartut.runtime.Runtime;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.instrumentation.MethodCallReplacementCache;
import org.smartut.runtime.mock.SmartUtMock;
import org.smartut.runtime.mock.MockFramework;
import org.smartut.runtime.mock.java.lang.MockThrowable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.with.different.packagename.agent.ExceptionHolder;

public class InstrumentingAgent_exceptionsIntTest {

	private final boolean replaceCalls = RuntimeSettings.mockJVMNonDeterminism;

	public static NullPointerException getNPE(){
		return new NullPointerException("This shouldn't be mocked");
	}


	@BeforeClass
	public static void initClass(){
		InstrumentingAgent.initialize();
	}

	@Before
	public void storeValues() {
		RuntimeSettings.mockJVMNonDeterminism = true;
        MethodCallReplacementCache.resetSingleton();
		Runtime.getInstance().resetRuntime();
	}

	@After
	public void resetValues() {
		RuntimeSettings.mockJVMNonDeterminism = replaceCalls;
	}


	@Test
	public void testExceptions(){

		Object obj = null;

		try{
			InstrumentingAgent.activate();
			obj = new ExceptionHolder();
		} finally {
			InstrumentingAgent.deactivate();
		}

		try{
			MockFramework.enable();
			ExceptionHolder eh = (ExceptionHolder) obj;

			Assert.assertFalse(eh.getNonMockedNPE() instanceof SmartUtMock);
			Assert.assertTrue(eh.getMockedThrowable() instanceof SmartUtMock);

			StackTraceElement[] traces = new MockThrowable().getStackTrace();

			StackTraceElement[] a = eh.getTracesWhenCast();
			Assert.assertEquals(traces[1], a[1]);
			
		} finally{
			MockFramework.disable();
		}
	}


	@Test
	public void testStaticClassExceptions(){

		Object obj = null;

		try{
			InstrumentingAgent.activate();			
			obj = new ExceptionHolder.StaticPublicException();
		} finally {
			InstrumentingAgent.deactivate();
		}

		try{
			MockFramework.enable();

			Exception foo = (ExceptionHolder.StaticPublicException) obj;
			Assert.assertTrue(foo instanceof SmartUtMock);		
		} finally{
			MockFramework.disable();
		}
	}
	
	@Test
	public void testReplacementCallInSubClassOfException(){
		Object obj = null;

		try{
			InstrumentingAgent.activate();
			obj = new ExceptionHolder();
		} finally {
			InstrumentingAgent.deactivate();
		}

		try{
			MockFramework.enable();
			
			StackTraceElement[] traces = new MockThrowable().getStackTrace();
			
			ExceptionHolder eh = (ExceptionHolder) obj;
			StackTraceElement[] b = eh.getTraces();
			Assert.assertEquals(traces[1], b[1]);

		} finally{
			MockFramework.disable();
		}

	}
	
}
