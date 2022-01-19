package org.smartut.basic;

import com.examples.with.different.packagename.NonNull;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class NonNullSystemTest extends SystemTestBase {

    @Test
    public void testNonNull() {
        SmartUt smartut = new SmartUt();

        String targetClass = NonNull.class.getCanonicalName();
        Properties.NULL_PROBABILITY = 1.0;
        Properties.TARGET_CLASS = targetClass;
        Properties.HONOUR_DATA_ANNOTATIONS = true;
        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertFalse(best.toString().contains("(Object) null"));
        Assert.assertFalse(best.toString().contains("null"));

    }
}
