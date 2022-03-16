package org.smartut.basic;

import com.examples.with.different.packagename.TargetMethod;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TargetMethodSystemTest extends SystemTestBase {

    @Test
    public void testTargetMethodWithBranchCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.BRANCH};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithIBranchCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.IBRANCH};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithCBranchCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.CBRANCH};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithOutputCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.OUTPUT};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithExceptionCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.EXCEPTION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithStrongMutationCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.STRONGMUTATION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithWeakMutationCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.WEAKMUTATION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithMethodTraceCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.METHODTRACE};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithMethodCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.METHOD};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithMethodNoExceptionCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.METHODNOEXCEPTION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithLineCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.LINE};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithOnlyLineCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.ONLYLINE};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }


    @Test
    public void testTargetMethodWithInputCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.INPUT};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Ignore // Why?
    @Test
    public void testTargetMethodWithALLDEFCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.ALLDEFS};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Test
    public void testTargetMethodWithDEFUSECoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.DEFUSE};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    //@Ignore
    @Test
    public void testTargetMethodWithOnlyBranchCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.ONLYBRANCH};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());
        System.out.println(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    @Ignore
    @Test // No goals generated
    public void testTargetMethodWithTryCatchCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.TRYCATCH};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();

        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    // @Ignore
    @Test //TODO: Needs to be fixed
    public void testTargetMethodWithStatementCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.STATEMENT};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    // @Ignore
    @Test //TODO: Needs to be fixed
    public void testTargetMethodWithMutationCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.MUTATION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }

    // @Ignore
    @Test // Todo: Needs to be fixed
    public void testTargetMethodWithOnlyMutationCoverage() {
        SmartUt smartut = new SmartUt();

        String targetClass = TargetMethod.class.getCanonicalName();
        String targetMethod = "foo(Ljava/lang/Integer;)Z";
        Properties.TARGET_CLASS = targetClass;
        Properties.TARGET_METHOD = targetMethod;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.ONLYMUTATION};
        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println(best.toString());
        Assert.assertTrue(best.toString().contains("foo"));
        Assert.assertFalse(best.toString().contains("bar"));
    }
}
