package org.smartut.basic;

import com.examples.with.different.packagename.Java9ExcludedPackage;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class Java9ExcludedPackagesSystemTest extends SystemTestBase {
    @Test
    public void testSunGraphics2DPackage() {
        SmartUt smartut = new SmartUt();

        String targetClass = Java9ExcludedPackage.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.print(best.toString());
        Assert.assertFalse(best.toString().contains("SunGraphics2D"));
        Assert.assertTrue(best.toString().contains("testMe"));
    }
}
