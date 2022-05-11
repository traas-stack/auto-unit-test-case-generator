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
package org.smartut.mock.java.net;

import com.examples.with.different.packagename.mock.java.net.*;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Created by arcuri on 12/19/14.
 */
public class MockTcpSystemTest extends SystemTestBase {

    private static final boolean VNET = Properties.VIRTUAL_NET;

    @After
    public void restoreProperties(){
        Properties.VIRTUAL_NET = VNET;
    }


    @Test
    public void testReceiveTcp_exception_onlyLine(){
        SmartUt smartut = new SmartUt();


        String targetClass = ReceiveTcp_exception.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 20000;
        Properties.VIRTUAL_NET = true;

        Properties.CRITERION = new Properties.Criterion[]{
                Properties.Criterion.LINE,
        };


        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        // should be only one test, as any exception thrown would lead to lower coverage
        Assert.assertEquals(1 , best.getTests().size());
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

    @Test
    public void testReceiveTcp_exception_tryCatch(){
        SmartUt smartut = new SmartUt();


        String targetClass = ReceiveTcp_exception_tryCatch.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 100_000;
        Properties.VIRTUAL_NET = true;

        Properties.CRITERION = new Properties.Criterion[]{
                Properties.Criterion.LINE,
        };


        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        /*
            there should be two tests:
            - that covers the catch block (which has a return)
            - one with no exception
          */
        Assert.assertEquals(2 , best.getTests().size());
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

    @Test
    public void testReceiveTcp_exception(){
        SmartUt smartut = new SmartUt();


        String targetClass = ReceiveTcp_exception.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 20000;
        Properties.VIRTUAL_NET = true;

        Properties.CRITERION = new Properties.Criterion[]{
                Properties.Criterion.LINE,
                Properties.Criterion.EXCEPTION
        };


        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        /*
            should be 2 tests:
            - one with exception
            - one with full line coverage

            fitness for line: 0
            fitness for exception:  1/(1+1) = 0.5
          */
        Assert.assertEquals(2 , best.getTests().size());
        Assert.assertEquals("Unexpected fitness: ", 0.5d, best.getFitness(), 0.001);
    }


    //TODO put back once we properly handle boolean functions with TT
    @Ignore
    @Test
    public void testReceiveTcp_noBranch(){
        SmartUt smartut = new SmartUt();

        /*
            as there is no branch, covering both boolean outputs with online line coverage
            would not be possible. and so output coverage would be a necessity
         */
        String targetClass = ReceiveTcp_noBranch.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 20000;
        Properties.VIRTUAL_NET = true;

        Properties.CRITERION = new Properties.Criterion[]{
                Properties.Criterion.LINE,
                Properties.Criterion.OUTPUT
        };

        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        List<TestFitnessFactory<? extends TestFitnessFunction>> list= TestGenerationStrategy.getFitnessFactories();

        Assert.assertEquals(2 , list.size());
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }


    @Test
    public void testReceiveTcp(){
        SmartUt smartut = new SmartUt();

        String targetClass = ReceiveTcp.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 200_000;
        Properties.VIRTUAL_NET = true;

        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

    @Test
    public void testSendTcp(){
        SmartUt smartut = new SmartUt();

        String targetClass = SendTcp.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 20000;
        Properties.VIRTUAL_NET = true;

        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        Assert.assertNotNull(result);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

}
