/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * Copyright (C) 2021- SmartUt contributors
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
package org.smartut.mock.java.lang;

import com.examples.with.different.packagename.mock.java.lang.SourceExceptions;
import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.TestCaseExecutor;
import org.smartut.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Andrea Arcuri on 19/08/15.
 */
public class SourceExceptionsSystemTest extends SystemTestBase {

    @Test
    public void testRuntimeException() {
        String targetClass = SourceExceptions.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.REPLACE_CALLS = true;
        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.LINE, Properties.Criterion.EXCEPTION};

        SmartUt smartut = new SmartUt();
        String[] command = new String[]{"-generateSuite", "-class", targetClass};
        Object result = smartut.parseCommandLine(command);

        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        Assert.assertNotNull(best);
        TestCaseExecutor.getInstance().initExecutor();
        for(TestChromosome test : best.getTestChromosomes()) {
        	ExecutionResult executionResult = TestCaseExecutor.getInstance().runTest(test.getTestCase());
        	test.setLastExecutionResult(executionResult);
        }

        String code = best.toString();
        Assert.assertTrue("Code:\n"+code, code.contains("verifyException(\"com.examples.with.different.packagename.mock.java.lang.SourceExceptions\","));
        Assert.assertTrue("Code:\n"+code, code.contains("verifyException(\"com.examples.with.different.packagename.mock.java.lang.SourceExceptions$Foo\","));
    }
}
