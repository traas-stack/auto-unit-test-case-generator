package org.smartut.setup;

import com.examples.with.different.packagename.interfaces.InterfaceWithDefaultMethods;
import com.examples.with.different.packagename.interfaces.InterfaceWithStaticMethods;
import com.examples.with.different.packagename.interfaces.InterfaceWithoutSubclasses;
import com.examples.with.different.packagename.interfaces.StandardInterface;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.strategy.TestGenerationStrategy;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class InterfaceSystemTest extends SystemTestBase {

    @Test
    public void testInterfaceWithoutSubclasses() {
        SmartUt smartut = new SmartUt();

        String targetClass = InterfaceWithoutSubclasses.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        String[] command = new String[] { "-generateSuite", "-class",
                targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertEquals(0.0, best.getFitness(), 0.0);

        for(TestFitnessFactory<? extends TestFitnessFunction> ff : TestGenerationStrategy.getFitnessFactories()) {
            Assert.assertEquals(0, ff.getCoverageGoals().size());
        }
    }

    @Test
    public void testInterfaceWithSubclasses() {
        SmartUt smartut = new SmartUt();

        String targetClass = StandardInterface.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        String[] command = new String[] { "-generateSuite", "-class",
                targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertEquals(0.0, best.getFitness(), 0.0);

        for(TestFitnessFactory<? extends TestFitnessFunction> ff : TestGenerationStrategy.getFitnessFactories()) {
            Assert.assertEquals(0, ff.getCoverageGoals().size());
        }
    }

    @Test
    public void testInterfaceWithStaticMethods() {
        SmartUt smartut = new SmartUt();

        String targetClass = InterfaceWithStaticMethods.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.CRITERION = new Properties.Criterion[] {Properties.Criterion.METHOD, Properties.Criterion.BRANCH, Properties.Criterion.LINE};
        String[] command = new String[] { "-generateSuite", "-class",
                targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertEquals(0.0, best.getFitness(), 0.0);

        for(TestFitnessFactory<? extends TestFitnessFunction> ff : TestGenerationStrategy.getFitnessFactories()) {
            Assert.assertEquals(1, ff.getCoverageGoals().size());
        }
    }

    @Test
    public void testInterfaceWithDefaultMethods() {
        SmartUt smartut = new SmartUt();

        String targetClass = InterfaceWithDefaultMethods.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;
        Properties.CRITERION = new Properties.Criterion[] {Properties.Criterion.METHOD, Properties.Criterion.BRANCH, Properties.Criterion.LINE};
        String[] command = new String[] { "-generateSuite", "-class",
                targetClass };

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertEquals(0.0, best.getFitness(), 0.0);

        for(TestFitnessFactory<? extends TestFitnessFunction> ff : TestGenerationStrategy.getFitnessFactories()) {
            Assert.assertEquals(1, ff.getCoverageGoals().size());
        }
    }
}
