package org.smartut.testcase.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.execution.ExecutionTracer;

/**
 * Accurate Init Test Factory, generate init test case with particular init process
 * @author ryan.zjf
 * @date 2022/02/21
 */
public class AccurateInitTestFactory implements ChromosomeFactory<TestChromosome> {

    private static final long serialVersionUID = -5202578461625984100L;
    private static final int INIT_TEST_MAX_LENGTH = 50;

    /** Constant <code>logger</code> */
    protected static final Logger logger = LoggerFactory.getLogger(FixedLengthTestChromosomeFactory.class);

    /**
     * Creates a random test case (i.e., a test case consisting of random statements) with size which calculated by
     * {@code Properties.INIT_METHOD_SIZE} and private field, which means the structure of a test would like:
     * 1. object of class under test
     * 2. private field set
     * 3. test method invoke
     *
     * @return a random test case
     */
    private TestCase getRandomTestCase() {
        boolean tracerEnabled = ExecutionTracer.isEnabled();
        if (tracerEnabled)
            ExecutionTracer.disable();

        final TestCase test = getNewTestCase();
        final TestFactory testFactory = TestFactory.getInstance();

        // Then add random statements until the test case reaches the chosen length or we run out of
        // generation attempts.
        int testMethodSize = test.getTestMethodSize();
        int tryTimes = 0;

        /**
         * exit condition: method inserted size >= Properties.INIT_METHOD_SIZE
         * Or tryTimes >= INIT_TEST_MAX_LENGTH (in case of exception cause )
         */
        while(testMethodSize < Properties.INIT_METHOD_SIZE && tryTimes ++ < INIT_TEST_MAX_LENGTH) {
            // NOTE: Even though extremely unlikely, insertRandomStatement could fail every time
            // with return code -1, thus eventually exceeding MAX_ATTEMPTS. In this case, the
            // returned test case would indeed be empty!
            // testFactory.insertRandomStatement(test, test.size() - 1);
            testFactory.insertRandomCallStatement(test,test.size() - 1);

            // 更新testMethodSize
            testMethodSize = test.getTestMethodSize();
        }

        if (logger.isDebugEnabled())
            logger.debug("Randomized test case:" + test.toCode());

        if (tracerEnabled)
            ExecutionTracer.enable();

        return test;
    }

    /**
     * {@inheritDoc}
     *
     * Generate a random chromosome
     */
    @Override
    public TestChromosome getChromosome() {
        TestChromosome c = new TestChromosome();
        c.setTestCase(getRandomTestCase());
        return c;
    }

    /**
     * Provided so that subtypes of this factory type can modify the returned
     * TestCase
     *
     * @return a {@link TestCase} object.
     */
    protected TestCase getNewTestCase() {
        return new DefaultTestCase(); // empty test case
    }

}
