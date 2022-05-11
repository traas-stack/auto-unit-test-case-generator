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

package org.smartut.testcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.smartut.Properties;
import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionObserver;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testcase.fm.MethodDescriptor;
import org.smartut.testcase.statements.FieldStatement;
import org.smartut.testcase.statements.FunctionalMockStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.ConstantValue;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testsuite.TestSuiteChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.utils.generic.GenericClass;

/**
 * Inline all primitive values and null references in the test case
 * 
 * @author Gordon Fraser
 */
public class ConstantInliner extends ExecutionObserver {

	private TestCase test = null;

	private static final Logger logger = LoggerFactory.getLogger(ConstantInliner.class);

	private boolean needAddNextFalseValue = false;

	public ConstantInliner() {}

	public ConstantInliner(boolean addNextFalseVal) {
		this.needAddNextFalseValue = addNextFalseVal;
	}

	/**
	 * <p>
	 * inline
	 * </p>
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestCase} object.
	 */
	public void inline(TestCase test) {
		this.test = test;
		TestCaseExecutor executor = TestCaseExecutor.getInstance();
		executor.addObserver(this);
		executor.execute(test);
		executor.removeObserver(this);
		removeUnusedVariables(test);
		assert (test.isValid());

	}

	/**
	 * <p>
	 * inline
	 * </p>
	 * 
	 * @param test
	 *            a {@link org.smartut.testcase.TestChromosome} object.
	 */
	public void inline(TestChromosome test) {
		inline(test.test);
	}

	/**
	 * <p>
	 * inline
	 * </p>
	 * 
	 * @param suite
	 *            a {@link org.smartut.testsuite.TestSuiteChromosome} object.
	 */
	public void inline(TestSuiteChromosome suite) {
		for (TestChromosome test : suite.getTestChromosomes()) {
			final int old_test_size = test.size();
			inline(test);
			final int new_test_size = test.size();
			final int removed_statements = old_test_size - new_test_size;
			if (removed_statements > 0) {
				ExecutionResult lastExecResult = test.getLastExecutionResult();
				if (lastExecResult != null) {
					final int old_exec_statements = lastExecResult.getExecutedStatements();
					final int new_exec_statements = old_exec_statements - removed_statements;
					lastExecResult.setExecutedStatements(new_exec_statements);
				}
			}
		}
	}

	/**
	 * Remove all unreferenced variables
	 * 
	 * @param t
	 *            The test case
	 * @return True if something was deleted
	 */
	public boolean removeUnusedVariables(TestCase t) {
		List<Integer> toDelete = new ArrayList<>();
		boolean hasDeleted = false;

		int num = 0;
		for (Statement s : t) {
			if (s instanceof PrimitiveStatement) {

				VariableReference var = s.getReturnValue();
				if (!t.hasReferences(var)) {
					toDelete.add(num);
					hasDeleted = true;
				}
			}
			num++;
		}
		toDelete.sort(Collections.reverseOrder());
		for (Integer position : toDelete) {
			t.remove(position);
		}

		return hasDeleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionObserver#output(int,
	 * java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void output(int position, String output) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.ExecutionObserver#statement(org.smartut.testcase.
	 * StatementInterface, org.smartut.testcase.Scope, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void afterStatement(Statement statement, Scope scope, Throwable exception) {
		try {
			for (VariableReference var : statement.getVariableReferences()) {
				if (var.equals(statement.getReturnValue())
						|| var.equals(statement.getReturnValue().getAdditionalVariableReference()))
					continue;
				Object object = var.getObject(scope);

				if (var.isPrimitive()) {
					ConstantValue value = new ConstantValue(test, var.getGenericClass());
					value.setValue(object);
					// logger.info("Statement before inlining: " +
					// statement.getCode());
					statement.replace(var, value);
					// logger.info("Statement after inlining: " +
					// statement.getCode());
				} else if (var.isString() && object != null) {
					ConstantValue value = new ConstantValue(test, var.getGenericClass());
					try {
						String val = StringEscapeUtils.unescapeJava(object.toString());
						if(val.length() < Properties.MAX_STRING) {
							value.setValue(val);
							statement.replace(var, value);
						}
					} catch (IllegalArgumentException e) {
						// Exceptions may happen if strings are not valid
						// unicode
						logger.info("Cannot escape invalid string: " + object);
					}
					// logger.info("Statement after inlining: " +
					// statement.getCode());
				} else if (var.isArrayIndex()) {
					// If this is an array index and there is an object outside
					// the array
					// then replace the array index with that object
					for (VariableReference otherVar : scope.getElements(var.getType())) {
						Object otherObject = otherVar.getObject(scope);
						if (otherObject == object && !otherVar.isArrayIndex()
								&& otherVar.getStPosition() < statement.getPosition()) {
							statement.replace(var, otherVar);
							break;
						}
					}
				} else {
					// TODO: Ignoring exceptions during getObject, but keeping
					// the assertion for now
					if (object == null) {
						if(statement instanceof MethodStatement) {
							MethodStatement ms = (MethodStatement)statement;
							if(var.equals(ms.getCallee())) {
								// Don't put null in callee's, the compiler will not accept it
								continue;
							}
						} else if(statement instanceof FieldStatement) {
							FieldStatement fs = (FieldStatement)statement;
							if(var.equals(fs.getSource())) {
								// Don't put null in source, the compiler will not accept it
								continue;
							}
						}
						ConstantValue value = new ConstantValue(test, var.getGenericClass());
						value.setValue(null);
						// logger.info("Statement before inlining: " +
						// statement.getCode());
						statement.replace(var, value);
						// logger.info("Statement after inlining: " +
						// statement.getCode());
					}
				}
			}
			/**
			 *
			 * target：    doReturn(true,true).when(iterator0).hasNext() may cause endless loop. Although it was
			 *             controlled by some param #Properties.MAX_LOOP_ITERATIONS，sometimes it will still cause OOM
			 * plan：      add false value as iterator doReturn last parameter
			 * algorithm： 1. find functionMockStatement
			 *             2. find mock method list - mockMethods in this statement
			 *             3. go through method list，find hasNext method
			 *             4. find param index map - methodParameterMap
			 *             5. find the parameter related to hasNext by index，
			 *                while param list is saved in EntityWithParametersStatement's parameters
			 *             6. construct false VariableReference，add it to parameters，and update index of methodParameterMap
			 *             7. go through the remaining mock method，map index +1，as parameter position shift right
			 *             8. notice that update count value in method descriptor，otherwise param position will be wrong
			 *                when executing mock statement
			 */
			if(needAddNextFalseValue && statement instanceof FunctionalMockStatement && Properties.GENERATE_FALSE_NEXT) {
				FunctionalMockStatement fms = (FunctionalMockStatement) statement;
				List<MethodDescriptor> mockMethods = fms.getMockedMethods();
				Map<String, int[]> methodParameterMap = fms.getMethodParameters();

				// method should be compatible with parameterMap
				if(mockMethods.size() != methodParameterMap.size()) {
					return;
				}

				for(int i = 0; i < mockMethods.size(); ++i) {
					MethodDescriptor md = mockMethods.get(i);
					// if hasNext, need add false value for guaranteeing loop
					if(md.getMethod().getName().equals("hasNext")) {

						// re-loop methodParameterMap every time, because minMax index has been modified
						Iterator<Entry<String, int[]>> iterator = methodParameterMap.entrySet().iterator();
						int mapIndex = 0;
						Entry<String, int[]> methodMapEntry = iterator.next();
						// find related map
						while(mapIndex++ < i) {
							// because has same size，not need to judge hasNext
							methodMapEntry = iterator.next();
						}

						// double check
						if(!methodMapEntry.getKey().contains("java.util.Iterator.hasNext")) {
							return;
						}

						// find index scope
						int[] minMax = methodMapEntry.getValue();
						if(minMax.length != 2) {
							return;
						}

						// judge has false already，if has，needn't to add false value
						List<VariableReference> list = fms.getParameterReferencesModifier();
						for(int refIndex = minMax[0]; refIndex <= minMax[1]; ++refIndex) {
							VariableReference var = list.get(refIndex);
							if(var instanceof ConstantValue) {
								ConstantValue value = (ConstantValue) var;
								if(value.getValue().equals(false)) {
									return;
								}
							}
						}

						Boolean falseObj = false;
						GenericClass genericClass = new GenericClass(falseObj.getClass());
						ConstantValue value = new ConstantValue(test, genericClass);
						value.setValue(false);

						// modify minMax first，max + 1；
						minMax[1] = minMax[1] + 1;

						// secondly, modify parameters, add variableReference at minMax[1] position,
						list.add(minMax[1], value);

						// Notice：method descriptor's counter need +1
						md.increaseCounter();

						// while added new parameters already，we need to adjust index of minMax in methodMap remaining
						while(iterator.hasNext()) {
							methodMapEntry = iterator.next();
							int[] minMaxAfter = methodMapEntry.getValue();

							// the remaining minMax + 1
							for(int index = 0; index < minMaxAfter.length; ++index) {
								minMaxAfter[index] = minMaxAfter[index] + 1;
							}
						}
					}
				}
			}
		} catch (CodeUnderTestException e) {
			logger.warn("Not inlining test: " + e.getCause());
			// throw new AssertionError("This case isn't handled yet: " +
			// e.getCause()
			// + ", " + Arrays.asList(e.getStackTrace()));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartut.testcase.ExecutionObserver#beforeStatement(org.smartut.
	 * testcase.StatementInterface, org.smartut.testcase.Scope)
	 */
	@Override
	public void beforeStatement(Statement statement, Scope scope) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.testcase.ExecutionObserver#clear()
	 */
	/** {@inheritDoc} */
	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		// do nothing
	}
}
