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
package org.smartut.runtime.sandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.*;

public class SandboxFromJUnitTest {

	private static ExecutorService executor;
	
	@BeforeClass
	public static void initSmartUtFramework(){
		Assert.assertNull(System.getSecurityManager());
		
		Sandbox.initializeSecurityManagerForSUT();
		executor = Executors.newCachedThreadPool();

	}
	
	@AfterClass
	public static void clearSmartUtFramework(){
		Assert.assertNotNull(System.getSecurityManager());	
		
		executor.shutdownNow();
		Sandbox.resetDefaultSecurityManager();

		Assert.assertNull(System.getSecurityManager());		
	}
	
	@Before
	public void initTest(){		
		Sandbox.goingToExecuteSUTCode();
        //TestGenerationContext.getInstance().goingToExecuteSUTCode();
	}
	
	@After
	public void doneWithTestCase(){
		Sandbox.doneWithExecutingSUTCode();	
	}
	
	
	@Test
	public void testExit() throws Exception{
		
		Future<?> future = executor.submit(new Runnable(){
			@Override
			public void run() {
		//-------
		Foo foo = new Foo();
		try{
			foo.tryToExit();
			Assert.fail();
		} catch(SecurityException e){
			//expected
		}
		//-------		
			}
		});
		future.get(5000, TimeUnit.MILLISECONDS);
		
	}
	
}


class Foo{
	
	public void tryToExit(){
		System.exit(0);
	}
}


