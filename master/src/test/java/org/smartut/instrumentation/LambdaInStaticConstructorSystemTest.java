package org.smartut.instrumentation;

import com.examples.with.different.packagename.LambdaInStaticConstructor;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Test;

public class LambdaInStaticConstructorSystemTest extends SystemTestBase {

    @Test
    public void testNoCrashInCLINIT() throws Throwable{
        String targetClass = LambdaInStaticConstructor.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.TIMEOUT = 50000000;
        Properties.RESET_STATIC_FINAL_FIELDS = true;

        SmartUt smartut = new SmartUt();
        String[] command = new String[] { "-generateSuite", "-class", targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);

        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());

    }
}
