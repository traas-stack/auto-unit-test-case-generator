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
package org.smartut.basic;

import com.examples.with.different.packagename.DependencyLibrary;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.runtime.instrumentation.RuntimeInstrumentation;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Andrea Arcuri on 21/04/15.
 */
public class DependencyLibrary_SystemTest extends SystemTestBase {

    @After
    public void reset(){
        RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(true);
    }

    @Test
    public void testSUTThatIsPartOfSmartUtDependencies() {

        //Chosen dependency might be among the shaded ones
        RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(false);

        SmartUt smartut = new SmartUt();

        String targetClass = DependencyLibrary.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.LINE};

        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);
        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("SmartUtTestSuite:\n" + best);

        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }
}
