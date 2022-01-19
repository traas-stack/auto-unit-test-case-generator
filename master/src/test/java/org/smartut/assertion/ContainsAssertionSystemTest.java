package org.smartut.assertion;

import com.examples.with.different.packagename.assertion.ContainerExample;
import org.smartut.SmartUt;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testcase.TestCase;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class ContainsAssertionSystemTest extends SystemTestBase {

    @Test
    public void testAssertionsIncludeContains() {

        //Properties.INLINE = false;
        SmartUt smartut = new SmartUt();

        String targetClass = ContainerExample.class.getCanonicalName();

        String[] command = new String[] {
                "-generateSuite", "-class", targetClass, "-Dassertion_strategy=all" };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome suite = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(suite.toString());

        Assert.assertTrue(suite.size() > 0);
        for (TestCase test : suite.getTests()) {
            boolean hasContainsAssertion = false;
            for(Assertion ass : test.getAssertions()) {
                if(ass instanceof ContainsAssertion) {
                    hasContainsAssertion = true;
                }
            }
            Assert.assertTrue("Test has no contains assertions: " + test.toCode(),
                    hasContainsAssertion);
        }
    }
}
