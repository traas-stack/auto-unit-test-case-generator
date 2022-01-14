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
package org.smartut.testsuite;

import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.Chromosome;
import org.smartut.ga.populationlimit.PopulationLimit;
import org.smartut.testcase.TestChromosome;


/**
 * <p>StatementsPopulationLimit class.</p>
 *
 * @author fraser
 */
public class StatementsPopulationLimit<T extends Chromosome<T>>
		implements PopulationLimit<T> {

	private static final long serialVersionUID = 4794704248615412859L;

	public StatementsPopulationLimit() {
	}

	/**
	 * Copy Constructor
	 *
	 * This constructor is used by {@link org.smartut.ga.metaheuristics.TestSuiteAdapter} to adapt the generic type
	 * parameter.
	 *
	 * This constructor shall preserve the current state of the StatementsPopulationLimit (if existing).
	 *
	 * @param other
	 */
	public StatementsPopulationLimit(StatementsPopulationLimit<?> other) {
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.PopulationLimit#isPopulationFull(java.util.List)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean isPopulationFull(List<T> population) {
		int numStatements = population.stream().map(x -> {
			if(x instanceof TestSuiteChromosome)
				return x;
			if(x instanceof TestChromosome)
				return ((TestChromosome) x).toSuite();
			throw new IllegalArgumentException("Could not transform population to TestSuites");
		})
				.mapToInt(x-> ((TestSuiteChromosome) x).totalLengthOfTestCases())
				.sum();
		return numStatements >= Properties.POPULATION;
	}

}
