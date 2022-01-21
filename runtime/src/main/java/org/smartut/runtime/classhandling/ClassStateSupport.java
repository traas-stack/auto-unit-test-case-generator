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
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.smartut.runtime.LoopCounter;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.agent.InstrumentingAgent;
import org.smartut.runtime.instrumentation.ExcludedClasses;
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

	//新增resetClasses的时间控制
	private static final long INIT_CLASS_TIME_OUT = 3L;

	//初始化的class进行缓存，reset时使用
	private static final List<String> initializedClasses = new ArrayList<>();

	//包含这些的class不进行reset
	private static final List<String> notResetClassContains = Arrays.asList("smartut", "MockitoMock", "EnhancerByMockito", "__CLR", "LoggerUtil");

	//以这些String结尾的class不进行reset
	private static final List<String> notResetClassSuffix = Arrays.asList("_SSTest", "scaffolding");

	private static final String[] externalInitMethods = new String[] {"$jacocoInit", "$gzoltarInit"};

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

		boolean problem = false;
		ClassResetter.getInstance().setClassLoader(classLoader);
		List<Class<?>> classes = loadClasses(classLoader, classNames);
		if(classes.size() != classNames.length) {
			problem = true;
		}

		initialiseExternalTools(classLoader, classes);

		if(RuntimeSettings.isUsingAnyMocking()) {

			for (Class<?> clazz : classes) {

                if(clazz.isInterface()) {
                    /*
                        FIXME: once we ll start to support Java 8, in which interfaces can have code,
                        we ll need to instrument them as well
                     */
                    continue;
                }

                if (!InstrumentedClass.class.isAssignableFrom(clazz)) {
                    String msg = "Class " + clazz.getName() + " was not instrumented by SmartUt. " +
                            "This could happen if you are running JUnit tests in a way that is not handled by SmartUt, in " +
                            "which some classes are loaded be reflection before the tests are run. Consult the SmartUt documentation " +
                            "for possible workarounds for this issue.";
                    logger.error(msg);
					problem = true;
                    //throw new IllegalStateException(msg); // throwing an exception might be a bit too extreme
                }
            }
        }

		return problem;

		//retransformIfNeeded(classes); // cannot do it, as retransformation does not really work :(
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
	 */
	public static void resetClasses() {
//		有可能卡住，reset classes增加超时控制
		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable call = () -> {
			// 把initClasses的类进行reset
				for(String initializedClassName : getLoadedClassesNeedReset()) {
					if(initializedClassName.isEmpty()) {
						continue;
					}
					ClassResetter.getInstance().reset(initializedClassName);
				}
			return null;
		};

		try {
			Future result = exec.submit(call);
			// 设定超时时间 3s超时
			result.get(INIT_CLASS_TIME_OUT, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			logger.warn("reset classes are timeout, time out seconds is {}", INIT_CLASS_TIME_OUT);
		} catch (Exception e) {
			logger.warn("reset classes meet exception {}", e.getMessage());
		}
		exec.shutdown();
	}

	/**
	 * 使用ClassResetter.classloader load的class list
	 * @return load classes
	 */
	private static Vector<Class<?>> getClassloaderLoadClasses(){
		Vector<Class<?>> classes = new Vector<>();
		try {
			ClassLoader classLoader = ClassResetter.getInstance().getClassLoader();
			Class<?> clazz = classLoader.getClass();
			while (clazz != java.lang.ClassLoader.class) {
				clazz = clazz.getSuperclass();
			}

			java.lang.reflect.Field ClassLoader_classes_field = clazz
					.getDeclaredField("classes");

			ClassLoader_classes_field.setAccessible(true);
			 classes = (Vector<Class<?>>) ClassLoader_classes_field.get(classLoader);

		}catch (Exception e){

		}
		return classes;
	}

	/**
	 * 需要进行reset的class
	 * 1.initializeClasses传人的class，在separateClassloader版本中initializeClasses传人的class为空
	 * 2.使用ClassResetter.classloader的class(过滤掉excluded.class中的class)
	 * @return
	 */
	private static List<String> getLoadedClassesNeedReset(){
		List<String> needResetClasses = new ArrayList<>();
		needResetClasses.addAll(initializedClasses);
		try {
			//获取使用ClassResetter.classloader的class
			needResetClasses.addAll(getClassloaderLoadClasses()
					.stream()
					//(过滤掉接口)
					.filter(Class::isInterface)
					.map(Class::getName)
					//根据class名字，过滤掉名字中包含notResetClassContains
					.filter(oneClassName->notResetClassContains.stream().anyMatch(oneClassName::contains)
							//根据class名字，过滤掉名字中以notResetClassSuffix结尾的class
							||notResetClassSuffix.stream().anyMatch(oneClassName::endsWith)
							//根据class名字，过滤excluded.class中的class
							|| ExcludedClasses.getPackagesShouldNotBeInstrumented().stream().anyMatch(oneClassName::startsWith))
					.collect(Collectors.toList()));
		} catch (Exception e) {
		}
		return needResetClasses;
	}

	/**
	 * 针对当前被测class进行reset
	 */
	public static void resetCUT() {
		ClassResetter.getInstance().reset(RuntimeSettings.className);
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
