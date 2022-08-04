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
package org.smartut.seeding.factories;

import org.smartut.ga.ChromosomeFactory;
import org.smartut.testsuite.TestSuiteChromosome;

public class ChromosomeSampleFactory implements ChromosomeFactory<TestSuiteChromosome> {
	public static final TestSuiteChromosome CHROMOSOME;
	private static final TestSampleFactory FACTORY;
    private static final long serialVersionUID = -5227032406625911394L;

    static {
		FACTORY = new TestSampleFactory();
		CHROMOSOME = new TestSuiteChromosome();
		for (int i = 0; i < 10; i++){
			CHROMOSOME.addTest(FACTORY.getChromosome());
		}
	}
	@Override
	public TestSuiteChromosome getChromosome() {
		// TODO Auto-generated method stub
		return CHROMOSOME.clone();
	}
	
	
}
