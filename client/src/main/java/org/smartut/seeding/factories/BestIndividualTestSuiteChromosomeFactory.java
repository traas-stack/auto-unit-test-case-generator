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

package org.smartut.seeding.factories;

import org.smartut.ga.ChromosomeFactory;
import org.smartut.testsuite.TestSuiteChromosome;

/**
 * 
 * @author Thomas White
 */
public class BestIndividualTestSuiteChromosomeFactory implements
		ChromosomeFactory<TestSuiteChromosome> {

	private static final long serialVersionUID = 1L;

	private final ChromosomeFactory<TestSuiteChromosome> defaultFactory;
	private final TestSuiteChromosome bestIndividual;
	private boolean seeded = false;

	/**
	 * <p>
	 * Constructor for JUnitTestSuiteChromosomeFactory.
	 * </p>
	 * 
	 * @param defaultFactory
	 *            a {@link org.smartut.ga.ChromosomeFactory} object.
	 */
	public BestIndividualTestSuiteChromosomeFactory(
			ChromosomeFactory<TestSuiteChromosome> defaultFactory,
			TestSuiteChromosome bestIndividual) {
		this.defaultFactory = defaultFactory;
		this.bestIndividual = bestIndividual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartut.ga.ChromosomeFactory#getChromosome()
	 */
	/** {@inheritDoc} */
	@Override
	public TestSuiteChromosome getChromosome() {
		if (!seeded){
			seeded = true;
			return bestIndividual.clone();
		}

		return defaultFactory.getChromosome();
	}

}
