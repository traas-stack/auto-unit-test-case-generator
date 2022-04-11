/*

 */
package org.smartut.junit.naming.methods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.smartut.Properties;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testsuite.TestSuiteChromosome;

public class MethodNameTestNameGenerationStrategy implements TestNameGenerationStrategy {

    private Map<TestCase, String> testToName = new HashMap<>();

    public MethodNameTestNameGenerationStrategy(List<TestCase> testCases, List<ExecutionResult> results) {
        generateNames(testCases);
    }

    public MethodNameTestNameGenerationStrategy(TestSuiteChromosome suite) {
        generateNames(suite.getTests());
    }

    private void generateNames(List<TestCase> testCases) {
        int totalNumberOfTests = testCases.size();
        String totalNumberOfTestsString = String.valueOf(totalNumberOfTests - 1);

        int num = 0;
        for(TestCase test : testCases) {
            String testNumber = StringUtils.leftPad(String.valueOf(num),
                    totalNumberOfTestsString.length(), "0");
            String testName;
            if(!Properties.TARGET_METHOD.isEmpty()){
                testName = String.format("test_%s_%s",Properties.TARGET_METHOD, testNumber);
            } else if(test.getTestMethodName() != null){
                testName = "test_" + test.getTestMethodName() + "_" + testNumber;
            } else {
                testName = "test" + testNumber;
            }

            testToName.put(test, testName);
            num++;
        }
    }

    @Override
    public String getName(TestCase test) {
        return testToName.get(test);
    }
}
