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

package org.smartut.testcase.localsearch;

import org.smartut.ga.localsearch.LocalSearchObjective;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.statements.EnumPrimitiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local search on enum values means we simply iterate over all possible values
 * the enum can take
 * 
 * @author Gordon Fraser
 */
public class EnumLocalSearch extends StatementLocalSearch {

	private static final Logger logger = LoggerFactory.getLogger(TestCaseLocalSearch.class);

	private Object oldValue;

	/* (non-Javadoc)
	 * @see org.smartut.testcase.LocalSearch#doSearch(org.smartut.testcase.TestChromosome, int, org.smartut.ga.LocalSearchObjective)
	 */
	/** {@inheritDoc} */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean doSearch(TestChromosome test, int statement,
	        LocalSearchObjective<TestChromosome> objective) {
		EnumPrimitiveStatement p = (EnumPrimitiveStatement) test.getTestCase().getStatement(statement);
		ExecutionResult oldResult = test.getLastExecutionResult();
		oldValue = p.getValue();

		for (Object value : p.getEnumValues()) {
			p.setValue(value);

			if (!objective.hasImproved(test)) {
				// Restore original
				p.setValue(oldValue);
				test.setLastExecutionResult(oldResult);
				test.setChanged(false);
			} else {
				logger.debug("Finished local search with result " + p.getCode());
				return true;
			}

		}

		return false;

	}

}
