/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and SmartUt
 * contributors
 *
 * This file is part of SmartUt.
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */
package org.smartut.junit.naming.methods;

import org.apache.commons.lang3.StringUtils;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testsuite.TestSuiteChromosome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gordon on 22/12/2015.
 */
public class NumberedTestNameGenerationStrategy implements TestNameGenerationStrategy {

    private Map<TestCase, String> testToName = new HashMap<>();

    public NumberedTestNameGenerationStrategy(List<TestCase> testCases, List<ExecutionResult> results) {
        generateNames(testCases);
    }

    public NumberedTestNameGenerationStrategy(TestSuiteChromosome suite) {
        generateNames(suite.getTests());
    }

    private void generateNames(List<TestCase> testCases) {
        int totalNumberOfTests = testCases.size();
        String totalNumberOfTestsString = String.valueOf(totalNumberOfTests - 1);

        int num = 0;
        for(TestCase test : testCases) {
            String testNumber = StringUtils.leftPad(String.valueOf(num),
                    totalNumberOfTestsString.length(), "0");
            String testName = "test" + testNumber;
            testToName.put(test, testName);
            num++;
        }
    }

    @Override
    public String getName(TestCase test) {
        return testToName.get(test);
    }
}
