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
package com.examples.with.different.packagename;

import org.smartut.runtime.annotation.SmartUtClassExclude;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

@SmartUtClassExclude
public class Euclidean_ESTest_scaffolding {

  private org.smartut.runtime.thread.ThreadStopper threadStopper =  new org.smartut.runtime.thread.ThreadStopper (org.smartut.runtime.thread.KillSwitchHandler.getInstance(), 3000);

  @BeforeClass 
  public static void initSmartUtFramework() { 
    org.smartut.runtime.RuntimeSettings.className = "com.examples.with.different.packagename.Euclidean";
    org.smartut.runtime.GuiSupport.initialize();
    org.smartut.runtime.RuntimeSettings.maxNumberOfThreads = 100;
    org.smartut.runtime.RuntimeSettings.maxNumberOfIterationsPerLoop = 10000;
    org.smartut.runtime.RuntimeSettings.mockSystemIn = true;
    org.smartut.runtime.RuntimeSettings.sandboxMode = org.smartut.runtime.sandbox.Sandbox.SandboxMode.RECOMMENDED;
    org.smartut.runtime.sandbox.Sandbox.initializeSecurityManagerForSUT();
    org.smartut.runtime.classhandling.JDKClassResetter.init();
    initializeClasses();
    org.smartut.runtime.Runtime.getInstance().resetRuntime();
  } 

  @Before 
  public void initTestCase(){ 
    threadStopper.storeCurrentThreads();
	threadStopper.startRecordingTime();
	org.smartut.runtime.jvm.ShutdownHookHandler.getInstance().initHandler();
	org.smartut.runtime.sandbox.Sandbox.goingToExecuteSUTCode();
	org.smartut.runtime.GuiSupport.setHeadless();
	org.smartut.runtime.Runtime.getInstance().resetRuntime();
	org.smartut.runtime.agent.InstrumentingAgent.activate();
  } 

  @After 
  public void doneWithTestCase(){ 
    threadStopper.killAndJoinClientThreads();
	org.smartut.runtime.jvm.ShutdownHookHandler.getInstance().safeExecuteAddedHooks();
	org.smartut.runtime.classhandling.JDKClassResetter.reset();
	org.smartut.runtime.sandbox.Sandbox.doneWithExecutingSUTCode();
	org.smartut.runtime.agent.InstrumentingAgent.deactivate();
	org.smartut.runtime.GuiSupport.restoreHeadlessMode();
  } 


  private static void initializeClasses() {
    org.smartut.runtime.classhandling.ClassStateSupport.initializeClasses(Euclidean_ESTest_scaffolding.class.getClassLoader() ,
      "com.examples.with.different.packagename.Euclidean"
    );
  } 
}
