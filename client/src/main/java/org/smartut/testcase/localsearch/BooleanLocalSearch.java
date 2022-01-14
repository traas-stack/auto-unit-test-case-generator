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

package org.smartut.testcase.localsearch;

import org.smartut.ga.localsearch.LocalSearchObjective;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.statements.PrimitiveStatement;

/**
 * <p>
 * BooleanLocalSearch class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public class BooleanLocalSearch extends StatementLocalSearch {

	private boolean oldValue;

	/* (non-Javadoc)
	 * @see org.smartut.testcase.LocalSearch#doSearch(org.smartut.testcase.TestChromosome, int, org.smartut.ga.LocalSearchObjective)
	 */
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public boolean doSearch(TestChromosome test, int statement,
	        LocalSearchObjective<TestChromosome> objective) {

		PrimitiveStatement<Boolean> p = (PrimitiveStatement<Boolean>) test.getTestCase().getStatement(statement);
		ExecutionResult oldResult = test.getLastExecutionResult();
		oldValue = p.getValue();

		p.setValue(!oldValue);

		if (!objective.hasImproved(test)) {
			// Restore original
			p.setValue(oldValue);
			test.setLastExecutionResult(oldResult);
			test.setChanged(false);
			return false;
		} else {
			return true;
		}
	}

}
