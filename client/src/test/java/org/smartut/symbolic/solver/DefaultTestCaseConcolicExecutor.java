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
package org.smartut.symbolic.solver;

import static org.smartut.symbolic.SymbolicObserverTest.printConstraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.smartut.Properties;
import org.smartut.symbolic.BranchCondition;
import org.smartut.symbolic.ConcolicExecution;
import org.smartut.symbolic.PathCondition;
import org.smartut.symbolic.expr.Constraint;
import org.smartut.symbolic.expr.Variable;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.IntegerVariable;
import org.smartut.symbolic.vm.ConstraintFactory;
import org.smartut.symbolic.vm.ExpressionFactory;
import org.smartut.testcase.DefaultTestCase;

public abstract class DefaultTestCaseConcolicExecutor {

	public static Collection<Constraint<?>> execute(DefaultTestCase tc) {
		List<BranchCondition> pc = getPathCondition(tc);

		Set<Variable<?>> variables = new HashSet<>();
		Collection<Constraint<?>> constraints = new LinkedList<>();
		for (BranchCondition condition : pc) {
			constraints.addAll(condition.getSupportingConstraints());
			Constraint<?> constraint = condition.getConstraint();
			constraints.add(constraint);
			variables.addAll(constraint.getVariables());
		}
		for (Variable<?> variable : variables) {
			if (variable instanceof IntegerVariable) {
				IntegerVariable integerVariable = (IntegerVariable) variable;
				IntegerConstant minValue = ExpressionFactory.buildNewIntegerConstant(integerVariable.getMinValue());
				IntegerConstant maxValue = ExpressionFactory.buildNewIntegerConstant(integerVariable.getMaxValue());
				constraints.add(ConstraintFactory.gte(integerVariable, minValue));
				constraints.add(ConstraintFactory.lte(integerVariable, maxValue));
			}
		}

		return constraints;
	}

	private static List<BranchCondition> getPathCondition(DefaultTestCase tc) {
		Properties.CLIENT_ON_THREAD = true;
		Properties.PRINT_TO_SYSTEM = true;
		Properties.TIMEOUT = 5000;
		Properties.CONCOLIC_TIMEOUT = 5000000;

		System.out.println("TestCase=");
		System.out.println(tc.toCode());

		PathCondition pc = ConcolicExecution.executeConcolic(tc);
		List<BranchCondition> branch_conditions = pc.getBranchConditions();

		printConstraints(branch_conditions);
		return branch_conditions;
	}

}
