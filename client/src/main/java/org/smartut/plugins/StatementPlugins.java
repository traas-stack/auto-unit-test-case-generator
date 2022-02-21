package org.smartut.plugins;

import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.statements.Statement;

/**
 * StatementPlugins serve as the plugins associated with statement and its subclasses
 *
 * author: Ding
 */
public interface StatementPlugins {

    /**
     * description: Set to a random value
     */
    void randomize(Statement statement);

    /**
     * <p>
     * mutate
     * </p>
     *
     * @param test
     *            a {@link org.smartut.testcase.TestCase} object.
     * @param factory
     *            a {@link org.smartut.testcase.TestFactory} object.
     * @return a boolean.
     */
    boolean mutate(Statement statement, TestCase test, TestFactory factory);

}
