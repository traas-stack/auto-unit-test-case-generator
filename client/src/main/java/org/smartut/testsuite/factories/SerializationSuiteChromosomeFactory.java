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
package org.smartut.testsuite.factories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.testcase.TestChromosome;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteSerialization;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationSuiteChromosomeFactory
    implements ChromosomeFactory<TestSuiteChromosome> {

    private static final long serialVersionUID = -569338946355072318L;

    private static final Logger logger = LoggerFactory.getLogger(SerializationSuiteChromosomeFactory.class);

    private List<TestChromosome> previousSuite = new ArrayList<>();

    private ChromosomeFactory<TestChromosome> defaultFactory;

    /**
     * The carved test cases are used only with a certain probability P. So, with probability 1-P the 'default' factory
     * is rather used.
     * 
     * @param defaultFactory
     * @throws IllegalStateException if Properties are not properly set
     */
    public SerializationSuiteChromosomeFactory(ChromosomeFactory<TestChromosome> defaultFactory)
        throws IllegalStateException {

        this.defaultFactory = defaultFactory;
        if (Properties.CTG_SEEDS_FILE_IN != null) {
            this.previousSuite.addAll(TestSuiteSerialization.loadTests(Properties.CTG_SEEDS_FILE_IN));
        } else {
        	this.previousSuite.addAll(TestSuiteSerialization.loadTests(Properties.SEED_DIR + File.separator + Properties.TARGET_CLASS));
        }
    }


    @Override
    public TestSuiteChromosome getChromosome() {

        TestSuiteChromosome tsc = new TestSuiteChromosome(this.defaultFactory);
        tsc.clearTests();

        if (Randomness.nextDouble() <= Properties.SEED_CLONE && this.previousSuite.size() > 0) {
            logger.debug("seeding previous test suite");

            for (TestChromosome tc : this.previousSuite) {
                TestChromosome clone = tc.clone();
                clone.getTestCase().removeAssertions(); // no assertions are used during search
                tsc.addTest(clone);
            }
        } else {
            logger.debug("creating a random testsuite");

            int numTests = Randomness.nextInt(Properties.MIN_INITIAL_TESTS, Properties.MAX_INITIAL_TESTS + 1);
            for (int i = 0; i < numTests; i++) {
                TestChromosome tc = this.defaultFactory.getChromosome();
                tsc.addTest(tc);
            }
        }

        assert(!tsc.getTestChromosomes().isEmpty());
        return tsc;
    }
}
