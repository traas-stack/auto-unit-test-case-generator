package org.smartut.runtime.mock;

/**
 * static method mock replace instrument
 */
public interface MethodStaticReplacementMock extends SmartUtMock {
    /**
     * Determine which class this mock is mocking
     *
     * @return a fully qualifying String
     */
    String getMockedClassName();
}
