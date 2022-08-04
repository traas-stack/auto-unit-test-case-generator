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
package org.smartut.coverage.readability;

import org.smartut.testcase.TestChromosome;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;

public class ReadabilitySuiteFitness extends TestSuiteFitnessFunction{


    private static final long serialVersionUID = 6243235746473531638L;


    @Override
    public double getFitness(TestSuiteChromosome suite)
    {
        double average = 0.0;

        for (TestChromosome ec : suite.getTestChromosomes()) {
            average += getScore(ec.toString());
        }

        average /= suite.getTestChromosomes().size();

        updateIndividual(suite, average);
        return average;
    }


    public double getScore(String test)
    {
        // TODO
        return 0.0;
    }


    @Override
    public boolean isMaximizationFunction() {
        return false;
    }
}
