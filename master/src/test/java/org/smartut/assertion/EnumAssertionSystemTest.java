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
package org.smartut.assertion;

import com.examples.with.different.packagename.assertion.ExampleReturningEnum;
import org.smartut.SmartUt;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testcase.TestCase;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gordon on 28/03/2016.
 */
public class EnumAssertionSystemTest extends SystemTestBase {

    @Test
    public void testAssertionsIncludeEnums() {

        SmartUt smartut = new SmartUt();

        String targetClass = ExampleReturningEnum.class.getCanonicalName();

        String[] command = new String[] {
                "-generateSuite", "-class", targetClass, "-Dassertion_strategy=all" };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome suite = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(suite.toString());

        Assert.assertTrue(suite.size() > 0);
        for (TestCase test : suite.getTests()) {
            boolean hasEnumAssertion = false;
            for(Assertion ass : test.getAssertions()) {
                if(ass instanceof PrimitiveAssertion) {
                    Assert.assertTrue(ass.getValue().getClass().isEnum());
                    hasEnumAssertion = true;
                }
            }
            Assert.assertTrue("Test has no enum assertions: " + test.toCode(),
                        hasEnumAssertion);
        }
        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size();
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, suite.getCoverage(), 0.05);
    }


    @Test
    public void testAssertionsPrefersEnums() {

        SmartUt smartut = new SmartUt();

        String targetClass = ExampleReturningEnum.class.getCanonicalName();

        String[] command = new String[] {
                "-generateSuite", "-class", targetClass, "-Dassertion_strategy=mutation" };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome suite = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(suite.toString());

        Assert.assertTrue(suite.size() > 0);
        for (TestCase test : suite.getTests()) {
            boolean hasEnumAssertion = false;
            for(Assertion ass : test.getAssertions()) {
                if(ass instanceof PrimitiveAssertion) {
                    Assert.assertTrue(ass.getValue().getClass().isEnum());
                    hasEnumAssertion = true;
                }
            }
            Assert.assertTrue("Test has no enum assertions: " + test.toCode(),
                    hasEnumAssertion);
        }
        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size();
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, suite.getCoverage(), 0.05);
    }
}
