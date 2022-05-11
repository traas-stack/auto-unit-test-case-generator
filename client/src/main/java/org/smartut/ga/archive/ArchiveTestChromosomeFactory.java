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
package org.smartut.ga.archive;

import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.factories.RandomLengthTestFactory;
import org.smartut.testsuite.TestSuiteSerialization;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ArchiveTestChromosomeFactory implements ChromosomeFactory<TestChromosome> {

  private static final long serialVersionUID = -8499807341782893732L;

  private final static Logger logger = LoggerFactory.getLogger(ArchiveTestChromosomeFactory.class);

  private ChromosomeFactory<TestChromosome> defaultFactory = new RandomLengthTestFactory();

  /**
   * Serialized tests read from disk, eg from previous runs in CTG
   */
  private List<TestChromosome> seededTests;

  public ArchiveTestChromosomeFactory() {
    if (Properties.CTG_SEEDS_FILE_IN != null) {
      //This does happen in CTG
      seededTests = TestSuiteSerialization.loadTests(Properties.CTG_SEEDS_FILE_IN);
      LoggingUtils.getSmartUtLogger().info("* Loaded {} tests from {}", seededTests.size(), Properties.CTG_SEEDS_FILE_IN);
    }
  }

  @Override
  public TestChromosome getChromosome() {

    if (seededTests != null && !seededTests.isEmpty()) {
      /*
              Ideally, we should populate the archive directly when SmartUt starts.
              But might be bit tricky based on current archive implementation (which needs executed tests).
              So, easiest approach is to just return tests here, with no mutation on those.
              However, this is done just once per test, as anyway those will end up
              in archive.
       */
      TestChromosome
          test =
          seededTests
              .remove(seededTests.size() - 1); //pull out one element, 'last' just for efficiency
      test.getTestCase().removeAssertions(); // no assertions are used during search
      return test;
    }

    TestChromosome test = null;
    // double P = (double)Archive.getArchiveInstance().getNumberOfCoveredTargets() / (double)Archive.getArchiveInstance().getNumberOfTargets();
    if (!Archive.getArchiveInstance().isArchiveEmpty()
        && Randomness.nextDouble() < Properties.SEED_CLONE) {
      logger.info("Creating test based on archive");
      test = new TestChromosome();
      test.setTestCase(Archive.getArchiveInstance().getRandomSolution().getTestCase());
      int mutations = Randomness.nextInt(Properties.SEED_MUTATIONS);
      for (int i = 0; i < mutations; i++) {
        test.mutate();
      }
    } else {
      logger.info("Creating random test");
      test = defaultFactory.getChromosome();
    }

    return test;
  }

}
