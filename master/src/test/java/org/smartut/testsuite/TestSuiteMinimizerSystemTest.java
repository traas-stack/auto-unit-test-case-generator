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

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.Properties.Criterion;
import org.smartut.SystemTestBase;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.coverage.MethodReturnsPrimitive;

public class TestSuiteMinimizerSystemTest extends SystemTestBase {
	
	private boolean oldMinimizeValues = Properties.MINIMIZE_VALUES;
	
	@After
	public void restoreProperties() {
		Properties.MINIMIZE_VALUES = oldMinimizeValues;
	}
	
	@Test
    public void testWithOneFitnessFunctionNoValueMinimization()
	{
		Properties.CRITERION = new Criterion[1];
        Properties.CRITERION[0] = Criterion.ONLYBRANCH;

        Properties.MINIMIZE_VALUES = false;

	    SmartUt smartut = new SmartUt();

        String targetClass = MethodReturnsPrimitive.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        String[] command = new String[] {
            "-generateSuite",
            "-class", targetClass
        };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome c = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(c.toString());
        
        Assert.assertEquals(0.0, c.getFitness(), 0.0);
        Assert.assertEquals(1.0, c.getCoverage(), 0.0);
        Assert.assertEquals(6.0, c.getNumOfCoveredGoals(ga.getFitnessFunction()), 0.0);
        Assert.assertEquals(5, c.size());
	}

    @Test
    public void testWithOneFitnessFunctionWithValueMinimization()
    {
        Properties.CRITERION = new Criterion[1];
        Properties.CRITERION[0] = Criterion.ONLYBRANCH;

        Properties.MINIMIZE_VALUES = true;
        Properties.MINIMIZE_SKIP_COINCIDENTAL = false;
        Properties.MINIMIZE_SECOND_PASS = false;
        SmartUt smartut = new SmartUt();

        String targetClass = MethodReturnsPrimitive.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        String[] command = new String[] {
                "-generateSuite",
                "-class", targetClass
        };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome c = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(c.toString());

        Assert.assertEquals(0.0, c.getFitness(), 0.0);
        Assert.assertEquals(1.0, c.getCoverage(), 0.0);
        Assert.assertEquals(6.0, c.getNumOfCoveredGoals(ga.getFitnessFunction()), 0.0);
        Assert.assertEquals(5, c.size());
    }

    @Test
    public void testWithOneFitnessFunctionWithValueMinimizationAndSkippingCoveredGoals()
    {
        Properties.CRITERION = new Criterion[1];
        Properties.CRITERION[0] = Criterion.ONLYBRANCH;

        Properties.MINIMIZE_VALUES = true;
        Properties.MINIMIZE_SKIP_COINCIDENTAL = true;
        Properties.MINIMIZE_SECOND_PASS = true;
        SmartUt smartut = new SmartUt();

        String targetClass = MethodReturnsPrimitive.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        String[] command = new String[] {
                "-generateSuite",
                "-class", targetClass
        };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome c = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(c.toString());

        Assert.assertEquals(0.0, c.getFitness(), 0.0);
        Assert.assertEquals(1.0, c.getCoverage(), 0.0);
        Assert.assertEquals(6.0, c.getNumOfCoveredGoals(ga.getFitnessFunction()), 0.0);
        Assert.assertEquals(5, c.size());
    }


	@Test
    public void testWithTwo()
	{
		Properties.CRITERION = new Criterion[2];
        Properties.CRITERION[0] = Criterion.ONLYBRANCH;
        Properties.CRITERION[1] = Criterion.LINE;

        Properties.MINIMIZE_VALUES = true;

	    SmartUt smartut = new SmartUt();

        String targetClass = MethodReturnsPrimitive.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        String[] command = new String[] {
            "-generateSuite",
            "-class", targetClass
        };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);

        TestSuiteChromosome c = (TestSuiteChromosome) ga.getBestIndividual();

        final FitnessFunction onlybranch = ga.getFitnessFunctions().get(0);
        final FitnessFunction line = ga.getFitnessFunctions().get(1);

        Assert.assertEquals(0.0, c.getFitness(onlybranch), 0.0);
        Assert.assertEquals(0.0, c.getFitness(line), 0.0);

        Assert.assertEquals(1.0, c.getCoverage(onlybranch), 0.0);
        Assert.assertEquals(1.0, c.getCoverage(line), 0.0);

        Assert.assertEquals(6.0, c.getNumOfCoveredGoals(onlybranch), 0.0);
        Assert.assertEquals(10.0, c.getNumOfCoveredGoals(line), 0.0);
	}
}
