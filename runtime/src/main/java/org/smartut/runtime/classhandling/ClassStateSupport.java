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

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.smartut.runtime.LoopCounter;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.agent.InstrumentingAgent;
import org.smartut.runtime.instrumentation.InstrumentedClass;
import org.smartut.runtime.sandbox.Sandbox;
import org.smartut.runtime.util.AtMostOnceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Class used to handle the static state of classes, their intialization and
 * re-instrumentation 
 * @author arcuri
 *
 */
public class ClassStateSupport {

	private static final Logger logger = LoggerFactory.getLogger(ClassStateSupport.class);

	private static final String[] externalInitMethods = new String[] {"$jacocoInit", "$gzoltarInit"};

	private static final long INIT_CLASS_TIME_OUT = 3L;

	private static final ExecutorService exec = Executors.newFixedThreadPool(3);

	private static final List<String> initializedClasses = new ArrayList<>();
    /**
     * Load all the classes with given name with the provided input classloader.
     * Those classes are all supposed to be instrumented.
	 *
	 * <p>
	 *     This method will usually be called in a @BeforeClass initialization
	 * </p>
	 *
     * @param classLoader
     * @param classNames
     */
	public static boolean initializeClasses(ClassLoader classLoader, String... classNames) {

		// initializeClasses may block, use thread pool and increase timeout control
		// Compatibility: set classloader at the beginning, and no explicit call is required during reset
		ClassResetter.getInstance().setClassLoader(classLoader);
		// Add the tested class to the array that needs init
		String[] classNamesWithCUT = addCutName(classNames);

		Callable<Boolean> call = () -> {
			//Start init
			boolean problem = false;

			List<Class<?>> classes = loadClasses(classLoader, classNamesWithCUT);
			initialiseExternalTools(classLoader, classes);

			if(RuntimeSettings.isUsingAnyMocking()) {
				problem =classes.stream().filter(Class::isInterface).anyMatch(clazz->!InstrumentedClass.class.isAssignableFrom(clazz));
			}
			// Record initializedClasses at the end
			initializedClasses.addAll(Arrays.asList(classNames));

			return problem;
		};

		try {
			Future<Boolean> result = exec.submit(call);
			// Set timeout
			Boolean obj = result.get(INIT_CLASS_TIME_OUT, TimeUnit.SECONDS);
			return obj;
		} catch (TimeoutException e) {
			logger.warn("initializing classes are timeout, time out seconds is {}", INIT_CLASS_TIME_OUT);
		} catch (Exception e) {
			logger.warn("initializing classes meet exception {}", e.getMessage());
		}

		exec.shutdown();
		return false;
	}

	private static String[] addCutName(String... classNames){
		String[] classNamesWithCUT = new String[classNames.length + 1];
		System.arraycopy(classNames, 0, classNamesWithCUT, 0, classNames.length);
		classNamesWithCUT[classNames.length] = org.smartut.runtime.RuntimeSettings.className;
		return classNamesWithCUT;
	}

	/*
	 * If a class is instrumented by Jacoco, GZoltar, or any other similar coverage-based
	 * tool, we need to make sure it is initialised so that the shutdownhook is added before
	 * the first test is executed.
	 */
	private static void initialiseExternalTools(ClassLoader classLoader, List<Class<?>> classes) {

		for (String externalInitMethod : externalInitMethods) {
			for(Class<?> clazz : classes) {
				try {
					Method initMethod = clazz.getDeclaredMethod(externalInitMethod);
					logger.error("Found {} in class {}", externalInitMethod, clazz.getName());
					initMethod.setAccessible(true);
					initMethod.invoke(null);
					// Once it has been invoked the agent should be loaded and we're done
					break;
				} catch (NoSuchMethodException e) {
					// No instrumentation, no need to do anything
				} catch (Throwable e) {
					logger.info("Error while checking for {} in class {}: {}", externalInitMethod, clazz.getName(), e.getMessage());

				}
			}
		}
	}

	/**
	 * Reset the static state of all the given classes.
	 *
	 * <p>
	 *     This method will be usually called after a test is executed, ie in a @After
	 * </p>
	 *
	 * @param classNames
	 */
	public static void resetClasses(String... classNames) {
		for (String classNameToReset : classNames) {
			ClassResetter.getInstance().reset(classNameToReset);
		}
	}


	private static List<Class<?>> loadClasses(ClassLoader classLoader, String... classNames) {

		List<Class<?>> classes = new ArrayList<>();

		InstrumentingAgent.activate();
		boolean safe = Sandbox.isSafeToExecuteSUTCode();

		//assert !Sandbox.isSecurityManagerInitialized() || Sandbox.isOnAndExecutingSUTCode();

		for (final String className : classNames) {

			org.smartut.runtime.Runtime.getInstance().resetRuntime();

			Sandbox.goingToExecuteSUTCode();
			boolean wasLoopCheckOn = LoopCounter.getInstance().isActivated();

			try {
				if (!safe) {
					Sandbox.goingToExecuteUnsafeCodeOnSameThread();
				}
				LoopCounter.getInstance().setActive(false);
				Class<?> aClass = Class.forName(className, true, classLoader);
				classes.add(aClass);

			} catch (Exception | Error ex) {
				AtMostOnceLogger.error(logger, "Could not initialize " + className + ": " + ex.getMessage());
			} finally {
				if (!safe) {
					Sandbox.doneWithExecutingUnsafeCodeOnSameThread();
				}
				Sandbox.doneWithExecutingSUTCode();
				LoopCounter.getInstance().setActive(wasLoopCheckOn);
			}
		}
		InstrumentingAgent.deactivate();
		return classes;
	}



	// deprecated ---------------------------------

	/**
	 * If any of the loaded class was not instrumented yet, then re-instrument them.
	 * Note: re-instrumentation is more limited, as cannot change class signature
	 */
	@Deprecated
	public static void retransformIfNeeded(ClassLoader classLoader, String... classNames) {
		List<Class<?>> classes = new ArrayList<>();
		for(String name : classNames) {
			try {
				classes.add(classLoader.loadClass(name));
			} catch (ClassNotFoundException e) {
				java.lang.System.err.println("Could not load: "+name);
			}
		}
		retransformIfNeeded(classes);
	}

	/**
	 * If any of the loaded class was not instrumented yet, then re-instrument them.
	 * Note: re-instrumentation is more limited, as cannot change class signature
	 * @param classes
	 */
	@Deprecated
	public static void retransformIfNeeded(List<Class<?>> classes) {

		if(classes==null || classes.isEmpty()){
			return;
		}

		List<Class<?>> classToReInstrument = new ArrayList<>();

		/*
		InstrumentingAgent.activate();
		for(Class<?> cl : classes){

			try{
				InstrumentingAgent.getInstumentation().retransformClasses(cl);
			} catch(UnsupportedOperationException e){
				/ *
				 * this happens if class was already loaded by JUnit (eg the abstract class problem)
				 * and re-instrumentation do change the signature
				 * /
				classToReInstument.add(cl);
			} catch(Exception | Error e){
				//this shouldn't really happen
				java.lang.System.err.println("Could not instrument "+cl.getName()+". Exception "+e.toString());
			}

		}
		*/

		for(Class<?> cl : classes) {
			if(!InstrumentingAgent.getTransformer().isClassAlreadyTransformed(cl.getName())) {
				classToReInstrument.add(cl);
			}
		}

		if(classToReInstrument.isEmpty()) {
			return;
		}

		InstrumentingAgent.setRetransformingMode(true);
		try {
			if(!classToReInstrument.isEmpty()) {
				InstrumentingAgent.getInstrumentation().retransformClasses(classToReInstrument.toArray(new Class<?>[0]));
			}
		} catch (UnmodifiableClassException e) {
			//this shouldn't really happen, as already checked in previous loop
			java.lang.System.err.println("Could not re-instrument classes");
		} catch(UnsupportedOperationException e) {
			//if this happens, then it is a bug in SmartUt :(
			logger.error("SmartUt wrong re-instrumentation: "+e.getMessage());
		} finally {
			InstrumentingAgent.setRetransformingMode(false);
		}

		InstrumentingAgent.deactivate();
	}
}
