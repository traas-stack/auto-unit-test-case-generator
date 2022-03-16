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

package org.smartut;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.smartut.assertion.InspectorManager;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.contracts.ContractChecker;
import org.smartut.contracts.FailingTestSet;
import org.smartut.coverage.branch.BranchPool;
import org.smartut.coverage.dataflow.DefUsePool;
import org.smartut.coverage.mutation.MutationPool;
import org.smartut.coverage.mutation.MutationTimeoutStoppingCondition;
import org.smartut.ga.archive.Archive;
import org.smartut.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.graphs.GraphPool;
import org.smartut.graphs.cfg.BytecodeInstructionPool;
import org.smartut.graphs.cfg.CFGMethodAdapter;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.instrumentation.LinePool;
import org.smartut.runtime.Runtime;
import org.smartut.runtime.classhandling.ModifiedTargetStaticFields;
import org.smartut.runtime.instrumentation.MethodCallReplacementCache;
import org.smartut.runtime.instrumentation.RemoveFinalClassAdapter;
import org.smartut.runtime.util.JOptionPaneInputs;
import org.smartut.runtime.util.SystemInUtil;
import org.smartut.seeding.CastClassManager;
import org.smartut.seeding.ConstantPoolManager;
import org.smartut.seeding.ObjectPoolManager;
import org.smartut.setup.ConcreteClassAnalyzer;
import org.smartut.setup.DependencyAnalysis;
import org.smartut.setup.TestCluster;
import org.smartut.setup.TestClusterGenerator;
import org.smartut.symbolic.DSEStats;
import org.smartut.testcarver.extraction.CarvingManager;
import org.smartut.testcase.execution.ExecutionTracer;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.execution.reset.ClassReInitializer;
import org.smartut.utils.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gordon Fraser
 * 
 */
public class TestGenerationContext {

	private static final Logger logger = LoggerFactory.getLogger(TestGenerationContext.class);

	private static final TestGenerationContext singleton = new TestGenerationContext();

	/**
	 * This is the classloader that does the instrumentation - it needs to be
	 * used by all test code
	 */
	private InstrumentingClassLoader classLoader;

	/**
	 * The classloader used to load this class
	 */
	private ClassLoader originalClassLoader;

	/**
	 * To avoid duplicate analyses we cache the cluster generator
	 */
	private TestClusterGenerator testClusterGenerator;

	/**
	 * Private singleton constructor
	 */
	private TestGenerationContext() {
		originalClassLoader = this.getClass().getClassLoader();
		classLoader = new InstrumentingClassLoader();
	}

	public static TestGenerationContext getInstance() {
		return singleton;
	}

	/**
	 * This is pretty important if the SUT use classloader of the running
	 * thread. If we do not set this up, we will end up with cast exceptions.
	 *
	 * <p>
	 * Note, an example in which this happens is in
	 *
	 * <p>
	 * org.dom4j.bean.BeanAttribute
	 *
	 * <p>
	 * in SF100 project 62_dom4j
	 */
	public void goingToExecuteSUTCode() {

		Thread.currentThread().setContextClassLoader(classLoader);
	}

	public void doneWithExecutingSUTCode() {
		Thread.currentThread().setContextClassLoader(originalClassLoader);
	}

	public InstrumentingClassLoader getClassLoaderForSUT() {
		return classLoader;
	}

	public TestClusterGenerator getTestClusterGenerator() {
		return testClusterGenerator;
	}

	public void setTestClusterGenerator(TestClusterGenerator generator) {
	    testClusterGenerator = generator;
    }

	/**
	 * @deprecated use {@code getInstance().getClassLoaderForSUT()}
	 *
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return getInstance().classLoader;
	}

	public void resetContext() {
		logger.info("*** Resetting context");

		// A fresh context needs a fresh class loader to make sure we can
		// re-instrument classes
		classLoader = new InstrumentingClassLoader();

		TestCaseExecutor.pullDown();

		ExecutionTracer.getExecutionTracer().clear();

		// TODO: BranchPool should not be static
		BranchPool.getInstance(classLoader).reset();
		RemoveFinalClassAdapter.reset();
		LinePool.reset();
		MutationPool.getInstance(classLoader).clear();

		// TODO: Clear only pool of current classloader?
		GraphPool.clearAll();
		DefUsePool.clear();

		// TODO: This is not nice
		for (ClassLoader cl : CFGMethodAdapter.methods.keySet())
			CFGMethodAdapter.methods.get(cl).clear();

		// TODO: Clear only pool of current classloader?
		BytecodeInstructionPool.clearAll();

		// TODO: After this, the test cluster is empty until
		// DependencyAnalysis.analyse is called
		TestCluster.reset();
		CastClassManager.getInstance().clear();
		ConcreteClassAnalyzer.getInstance().clear();
		// This counts the current level of recursion during test generation
		org.smartut.testcase.TestFactory.getInstance().reset();

		MaxStatementsStoppingCondition.setNumExecutedStatements(0);
		GlobalTimeStoppingCondition.forceReset();
		MutationTimeoutStoppingCondition.resetStatic();

		// Forget the old SUT
		Properties.resetTargetClass();

		TestCaseExecutor.initExecutor();

		Archive.getArchiveInstance().reset();

		// Constant pool
		ConstantPoolManager.getInstance().reset();
		ObjectPoolManager.getInstance().reset();
		CarvingManager.getInstance().clear();

		// TODO: Why are we doing this?
		if (Properties.INSTRUMENT_CONTEXT || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.DEFUSE)
				|| ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.IBRANCH)) {
			// || ArrayUtil.contains(Properties.CRITERION,
			// Properties.Criterion.CBRANCH)) {
			try {
				// 1. Initialize the callGraph before using
				String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
				DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
				testClusterGenerator = new TestClusterGenerator(
						DependencyAnalysis.getInheritanceTree());
				// 2. Use the callGraph
				testClusterGenerator.generateCluster(DependencyAnalysis.getCallGraph());
			} catch (RuntimeException e) {
				logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}

		if (Properties.CHECK_CONTRACTS) {
			FailingTestSet.changeClassLoader(classLoader);
		}
		ContractChecker.setActive(true);

		SystemInUtil.resetSingleton();
		JOptionPaneInputs.resetSingleton();
		Runtime.resetSingleton();
		MethodCallReplacementCache.resetSingleton();

		DSEStats.clear();

		// keep the list of initialized classes (clear them when needed in
		// the system test cases)
		final List<String> initializedClasses = ClassReInitializer.getInstance().getInitializedClasses();
		ClassReInitializer.resetSingleton();
		ClassReInitializer.getInstance().addInitializedClasses(initializedClasses);
		
		InspectorManager.resetSingleton();
		ModifiedTargetStaticFields.resetSingleton();
	}
}
