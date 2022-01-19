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
package org.smartut.mock.java.util;

import com.examples.with.different.packagename.mock.java.util.prefs.PrefsNode;
import com.examples.with.different.packagename.mock.java.util.prefs.PrefsSystem;
import com.examples.with.different.packagename.mock.java.util.prefs.PrefsUser;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gordon on 26/12/2016.
 */
public class PreferencesSystemTest extends SystemTestBase {

    @Test
    public void testUserPreferences() throws Exception{
        String targetClass = PrefsUser.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        SmartUt smartut = new SmartUt();
        String[] command = new String[] { "-generateSuite", "-class", targetClass };
        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        System.out.println(best.toString());

        Assert.assertNotNull(best);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

    @Test
    public void testSystemPreferences() throws Exception{
        String targetClass = PrefsSystem.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        SmartUt smartut = new SmartUt();
        String[] command = new String[] { "-generateSuite", "-class", targetClass };
        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        System.out.println(best.toString());

        Assert.assertNotNull(best);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }


    @Test
    public void testNodePreferences() throws Exception{
        String targetClass = PrefsNode.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        SmartUt smartut = new SmartUt();
        String[] command = new String[] { "-generateSuite", "-class", targetClass };
        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        System.out.println(best.toString());

        Assert.assertNotNull(best);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

}
