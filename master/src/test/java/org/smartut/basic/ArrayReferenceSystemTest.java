package org.smartut.basic;

import com.examples.with.different.packagename.ArrayReference;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class ArrayReferenceSystemTest extends SystemTestBase {

    public TestSuiteChromosome generateTest(boolean minimize){
        SmartUt smartut = new SmartUt();

        String targetClass = ArrayReference.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        Properties.ASSERTIONS = false;
        Properties.JUNIT_CHECK = Properties.JUnitCheckValues.FALSE;
        Properties.MINIMIZE = minimize;
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        return best;
    }

    @Test
    public void testArrayReferenceWithoutMinimization() {
        TestSuiteChromosome best = generateTest(false);
        Assert.assertFalse("Array reference should not be assigned to its first element", best.toString().contains("constructorArray0[0] = (Constructor<Insets>) constructorArray0;"));
    }

    @Test
    public void testArrayReferenceWithMinimization() {
        TestSuiteChromosome best = generateTest(true);
        Assert.assertFalse("Array reference should not be assigned to its first element", best.toString().contains("constructorArray0[0] = (Constructor<Insets>) constructorArray0;"));
    }

}
