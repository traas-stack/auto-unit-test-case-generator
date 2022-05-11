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
package org.smartut.basic;

import com.examples.with.different.packagename.InfiniteWhile;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.runtime.TooManyResourcesException;
import org.smartut.runtime.instrumentation.SmartUtClassLoader;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

/**
 * Created by Andrea Arcuri on 29/03/15.
 */
public class InfiniteWhile_SystemTest  extends SystemTestBase {

    @Test(timeout = 5000)
    public void testLoading() throws Exception{
        SmartUtClassLoader loader = new SmartUtClassLoader();
        Class<?> clazz = loader.loadClass(InfiniteWhile.class.getCanonicalName());
        Method m = clazz.getMethod("infiniteLoop");
        try {
            m.invoke(null);
            fail();
        }catch(InvocationTargetException e){
            //expected
            Assert.assertTrue(e.getCause() instanceof TooManyResourcesException);
        }
    }

    @Test(timeout = 30_000)
    public void systemTest(){

        SmartUt smartut = new SmartUt();

        String targetClass = InfiniteWhile.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 10;
        Properties.TIMEOUT = 5000;
        Properties.STOPPING_CONDITION = Properties.StoppingCondition.MAXTIME;
        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        System.out.println("EvolvedTestSuite:\n" + best);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }
    
    @Test(timeout = 30_000)
    public void systemTestJUnit(){

        SmartUt smartut = new SmartUt();

        String targetClass = InfiniteWhile.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.SEARCH_BUDGET = 10;
        Properties.TIMEOUT = 5000;
        Properties.STOPPING_CONDITION = Properties.StoppingCondition.MAXTIME;
        Properties.JUNIT_TESTS = true;
        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        System.out.println("EvolvedTestSuite:\n" + best);
        Assert.assertEquals("Should contain two tests: ", 2, best.size());
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }
}
