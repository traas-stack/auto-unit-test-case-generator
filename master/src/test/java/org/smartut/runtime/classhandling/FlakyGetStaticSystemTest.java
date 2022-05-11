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
package org.smartut.runtime.classhandling;

import com.examples.with.different.packagename.reset.ClassWithMutableStatic;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.statistics.OutputVariable;
import org.smartut.statistics.RuntimeVariable;
import org.smartut.statistics.backend.DebugStatisticsBackend;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by gordon on 20/02/2016.
 */
public class FlakyGetStaticSystemTest extends SystemTestBase {

    @Test
    public void testResetGetStatic() {

        Properties.RESET_STATIC_FIELDS = true;
        Properties.RESET_STATIC_FIELD_GETS = true;
        Properties.JUNIT_CHECK = Properties.JUnitCheckValues.TRUE;
        Properties.JUNIT_TESTS = true;
        Properties.SANDBOX = true;
        Properties.ASSERTION_STRATEGY = Properties.AssertionStrategy.ALL;

        SmartUt smartut = new SmartUt();

        String targetClass = ClassWithMutableStatic.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.OUTPUT_VARIABLES = "" + RuntimeVariable.HadUnstableTests;
        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);
        double best_fitness = best.getFitness();
        Assert.assertEquals("Optimal coverage was not achieved ", 0.0, best_fitness, 0.0);

        Map<String, OutputVariable<?>> map = DebugStatisticsBackend.getLatestWritten();
        Assert.assertNotNull(map);
        OutputVariable<?> unstable = map.get(RuntimeVariable.HadUnstableTests.toString());
        Assert.assertNotNull(unstable);
        Assert.assertEquals(Boolean.FALSE, unstable.getValue());
    }
}
