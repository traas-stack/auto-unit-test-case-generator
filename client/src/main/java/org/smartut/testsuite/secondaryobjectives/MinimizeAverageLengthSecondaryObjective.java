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
package org.smartut.testsuite.secondaryobjectives;

import org.smartut.ga.SecondaryObjective;
import org.smartut.testsuite.TestSuiteChromosome;

/**
 * <p>MinimizeAverageLengthSecondaryObjective class.</p>
 *
 * @author Gordon Fraser
 */
public class MinimizeAverageLengthSecondaryObjective extends SecondaryObjective<TestSuiteChromosome> {

	private static final long serialVersionUID = -6272641645062817112L;

	private double getAverageLength(TestSuiteChromosome chromosome) {
		return (double) chromosome.totalLengthOfTestCases()
		        / (double) chromosome.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.smartut.testcase.secondaryobjectives.SecondaryObjective#compareChromosomes(org.smartut.ga.Chromosome,
	 * org.smartut.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public int compareChromosomes(TestSuiteChromosome chromosome1, TestSuiteChromosome chromosome2) {
		return (int) Math.signum(getAverageLength(chromosome1)
		        - getAverageLength(chromosome2));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.smartut.testcase.secondaryobjectives.SecondaryObjective#compareGenerations(org.smartut.ga.Chromosome,
	 * org.smartut.ga.Chromosome, org.smartut.ga.Chromosome, org.smartut.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	public int compareGenerations(TestSuiteChromosome parent1, TestSuiteChromosome parent2,
			TestSuiteChromosome child1, TestSuiteChromosome child2) {
		return (int) Math.signum(Math.min(getAverageLength(parent1),
		                                  getAverageLength(parent2))
		        - Math.min(getAverageLength(child1), getAverageLength(child2)));
	}

}
