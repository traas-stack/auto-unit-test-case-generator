package org.smartut.basic;

import com.examples.with.different.packagename.MethodWithClassParameter;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class MethodWithClassParameterSystemTest extends SystemTestBase {

    @Test
    public void testIfClassParameterCanBeInstantiatedCorrectly() {
        SmartUt smartut = new SmartUt();

        String targetClass = MethodWithClassParameter.class.getCanonicalName();
        Properties.NULL_PROBABILITY = 1.0;
        Properties.TARGET_CLASS = targetClass;
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

//        System.out.println("EvolvedTestSuite:\n" + best);
        Assert.assertFalse(best.toString().contains("Class<Foo> class0 = Class.class;"));
        Assert.assertTrue(best.toString().contains("Class<Foo> class0 = Foo.class;"));

    }
}
