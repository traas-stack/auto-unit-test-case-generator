package org.smartut.plugins.testcase;

import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.statements.Statement;

/**
 * StatementPlugins serve as the plugins associated with statement and its subclasses
 *
 * @author: Ding
 */
public interface StatementPlugins {

    /**
     * description: Set to a random value
     *
     * @param statement
     *            a {@link org.smartut.testcase.statements.Statement} object.
     */
    void randomize(Statement statement);

    /**
     * <p>
     * mutate
     * </p>
     *
     * @param statement
     *            a {@link org.smartut.testcase.statements.Statement} object.
     * @param test
     *            a {@link org.smartut.testcase.TestCase} object.
     * @param factory
     *            a {@link org.smartut.testcase.TestFactory} object.
     * @return a boolean.
     */
    boolean mutate(Statement statement, TestCase test, TestFactory factory);

}
