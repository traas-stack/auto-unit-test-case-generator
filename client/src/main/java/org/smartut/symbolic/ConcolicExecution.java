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
package org.smartut.symbolic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.expr.ExpressionEvaluator;
import org.smartut.symbolic.instrument.ConcolicInstrumentingClassLoader;
import org.smartut.symbolic.vm.ArithmeticVM;
import org.smartut.symbolic.vm.CallVM;
import org.smartut.symbolic.vm.HeapVM;
import org.smartut.symbolic.vm.JumpVM;
import org.smartut.symbolic.vm.LocalsVM;
import org.smartut.symbolic.vm.OtherVM;
import org.smartut.symbolic.vm.PathConditionCollector;
import org.smartut.symbolic.vm.SymbolicFunctionVM;
import org.smartut.symbolic.vm.SymbolicEnvironment;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.dse.IVM;
import org.smartut.dse.MainConfig;
import org.smartut.dse.VM;

/**
 * <p>
 * ConcolicExecution class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public abstract class ConcolicExecution {

	private static final Logger logger = LoggerFactory.getLogger(ConcolicExecution.class);

	/** Instrumenting class loader */
	private static final ConcolicInstrumentingClassLoader classLoader = new ConcolicInstrumentingClassLoader();

	/**
	 * Retrieve the path condition for a given test case
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<BranchCondition> getSymbolicPath(TestChromosome test) {
		TestChromosome dscCopy = test.clone();
		DefaultTestCase defaultTestCase = (DefaultTestCase) dscCopy.getTestCase();

		PathCondition pathCondition = executeConcolic(defaultTestCase);
		return pathCondition.getBranchConditions();
	}

	public static PathCondition executeConcolic(DefaultTestCase defaultTestCase) {

		logger.debug("Preparing concolic execution");

		/**
		 * Prepare DSC configuration
		 */
		MainConfig.setInstance();

		/**
		 * Path constraint and symbolic environment
		 */
		SymbolicEnvironment env = new SymbolicEnvironment(classLoader);
		PathConditionCollector pc = new PathConditionCollector();

		/**
		 * VM listeners
		 */
		List<IVM> listeners = new ArrayList<>();
		listeners.add(new CallVM(env, classLoader));
		listeners.add(new JumpVM(env, pc));
		listeners.add(new HeapVM(env, pc, classLoader));
		listeners.add(new LocalsVM(env));
		listeners.add(new ArithmeticVM(env, pc));
		listeners.add(new OtherVM(env));
		listeners.add(new SymbolicFunctionVM(env, pc));
		VM.getInstance().setListeners(listeners);
		VM.getInstance().prepareConcolicExecution();

		defaultTestCase.getChangedClassLoader();
		defaultTestCase.changeClassLoader(classLoader);
		SymbolicObserver symbolicExecObserver = new SymbolicObserver(env);

		Set<ExecutionObserver> originalExecutionObservers = TestCaseExecutor.getInstance().getExecutionObservers();
		TestCaseExecutor.getInstance().newObservers();
		TestCaseExecutor.getInstance().addObserver(symbolicExecObserver);

		logger.info("Starting concolic execution");
		ExecutionResult result = new ExecutionResult(defaultTestCase, null);

		try {
			logger.debug("Executing test");

			long startConcolicExecutionTime = System.currentTimeMillis();
			result = TestCaseExecutor.getInstance().execute(defaultTestCase, Properties.CONCOLIC_TIMEOUT);

			long estimatedConcolicExecutionTime = System.currentTimeMillis() - startConcolicExecutionTime;
			DSEStats.getInstance().reportNewConcolicExecutionTime(estimatedConcolicExecutionTime);

			MaxStatementsStoppingCondition.statementsExecuted(result.getExecutedStatements());

		} catch (Exception e) {
			logger.error("Exception during concolic execution {}", e);
			return new PathCondition(new ArrayList<>());
		} finally {
			logger.debug("Cleaning concolic execution");
			TestCaseExecutor.getInstance().setExecutionObservers(originalExecutionObservers);
		}
		VM.disableCallBacks(); // ignore all callbacks from now on

		List<BranchCondition> branches = pc.getPathCondition();
		logger.info("Concolic execution ended with " + branches.size() + " branches collected");
		if (!result.noThrownExceptions()) {
			int idx = result.getFirstPositionOfThrownException();
			logger.info("Exception thrown: " + result.getExceptionThrownAtPosition(idx));
		}
		logNrOfConstraints(branches);

		logger.debug("Cleaning concolic execution");
		TestCaseExecutor.getInstance().setExecutionObservers(originalExecutionObservers);

		return new PathCondition(branches);
	}

	private static void logNrOfConstraints(List<BranchCondition> branches) {
		int nrOfConstraints = 0;

		ExpressionEvaluator exprExecutor = new ExpressionEvaluator();
		for (BranchCondition branchCondition : branches) {

			for (Constraint<?> supporting_constraint : branchCondition.getSupportingConstraints()) {
				supporting_constraint.getLeftOperand().accept(exprExecutor, null);
				supporting_constraint.getRightOperand().accept(exprExecutor, null);
				nrOfConstraints++;
			}

			Constraint<?> constraint = branchCondition.getConstraint();
			constraint.getLeftOperand().accept(exprExecutor, null);
			constraint.getRightOperand().accept(exprExecutor, null);
			nrOfConstraints++;

		}
		logger.debug("nrOfConstraints=" + nrOfConstraints);
	}
}
