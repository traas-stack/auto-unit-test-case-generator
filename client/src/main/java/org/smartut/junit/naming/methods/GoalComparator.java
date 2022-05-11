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
package org.smartut.junit.naming.methods;

import org.smartut.coverage.exception.ExceptionCoverageTestFitness;
import org.smartut.coverage.io.input.InputCoverageTestFitness;
import org.smartut.coverage.method.MethodCoverageTestFitness;
import org.smartut.coverage.method.MethodNoExceptionCoverageTestFitness;
import org.smartut.coverage.io.output.OutputCoverageTestFitness;
import org.smartut.testcase.TestFitnessFunction;

import java.util.Comparator;

/**
 * Created by gordon on 22/12/2015.
 */
public class GoalComparator implements Comparator<TestFitnessFunction> {

    // 1. MethodGoal
    // 2. Interface
    // 3. Exception
    // 4. Output
    // 5. Input
    // 6. Assertion

    @Override
    public int compare(TestFitnessFunction o1, TestFitnessFunction o2) {
        Class<?> c1 = o1.getClass();
        Class<?> c2 = o2.getClass();
        if(c1.equals(c2))
            return o1.compareTo(o2);

        if(c1.equals(ExceptionCoverageTestFitness.class))
            return -1;
        else if(c2.equals(ExceptionCoverageTestFitness.class))
            return 1;

        if(c1.equals(MethodCoverageTestFitness.class))
            return -1;
        else if(c2.equals(MethodCoverageTestFitness.class))
            return 1;

        if(c1.equals(MethodNoExceptionCoverageTestFitness.class))
            return -1;
        else if(c2.equals(MethodNoExceptionCoverageTestFitness.class))
            return 1;

        if(c1.equals(OutputCoverageTestFitness.class))
            return -1;
        else if(c2.equals(OutputCoverageTestFitness.class))
            return 1;

        if(c1.equals(InputCoverageTestFitness.class))
            return -1;
        else if(c2.equals(InputCoverageTestFitness.class))
            return 1;

        // TODO: Assertion

        return 0;
    }
}
