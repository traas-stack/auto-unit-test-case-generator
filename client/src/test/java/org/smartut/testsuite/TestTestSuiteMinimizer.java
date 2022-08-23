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
package org.smartut.testsuite;

import com.examples.with.different.packagename.FlagExample1;
import org.smartut.Properties;
import org.smartut.TestGenerationContext;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.coverage.branch.BranchCoverageFactory;
import org.smartut.coverage.branch.BranchCoverageSuiteFitness;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.reset.ClassReInitializer;
import org.smartut.testcase.statements.ConstructorStatement;
import org.smartut.testcase.statements.numeric.IntPrimitiveStatement;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.Randomness;
import org.smartut.utils.generic.GenericClass;
import org.smartut.utils.generic.GenericConstructor;
import org.smartut.utils.generic.GenericMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class TestTestSuiteMinimizer
{
    private static java.util.Properties currentProperties;

    @Before
    public void setUp()
    {
        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
        Properties.getInstance().resetToDefaults();
        Randomness.setSeed(42);
        TestGenerationContext.getInstance().resetContext();
        ClassReInitializer.resetSingleton();
        Randomness.setSeed(42);
        currentProperties = (java.util.Properties) System.getProperties().clone();
    }

    @After
    public void tearDown()
    {
        TestGenerationContext.getInstance().resetContext();
        System.setProperties(currentProperties);
        Properties.getInstance().resetToDefaults();
    }

    @Test
    public void minimizeEmptySuite() throws ClassNotFoundException
    {

        DefaultTestCase test = new DefaultTestCase();

        TestSuiteChromosome tsc = new TestSuiteChromosome();
        tsc.addTest(test);
        TestSuiteFitnessFunction ff = new BranchCoverageSuiteFitness();
        double previous_fitness = ff.getFitness(tsc);
        tsc.setFitness(ff, previous_fitness);
        assertEquals(0.0, previous_fitness, 0.0);

        TestSuiteMinimizer minimizer = new TestSuiteMinimizer(new BranchCoverageFactory());
        minimizer.minimize(tsc, false);
        assertEquals(0, tsc.getTests().size());

        double fitness = ff.getFitness(tsc);
        assertEquals(previous_fitness, fitness, 0.0);
    }

}
