/*
 * Copyright (C) 2010-2017 Gordon Fraser, Andrea Arcuri and SmartUt
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
package org.smartut.testsuite.secondaryobjectives;

import org.smartut.Properties;
import org.smartut.coverage.ibranch.IBranchSecondaryObjective;
import org.smartut.coverage.rho.RhoTestSuiteSecondaryObjective;
import org.smartut.ga.SecondaryObjective;
import org.smartut.testsuite.TestSuiteChromosome;


public class TestSuiteSecondaryObjective {


  public static void setSecondaryObjectives() {
    for (Properties.SecondaryObjective secondaryObjective : Properties.SECONDARY_OBJECTIVE) {
      SecondaryObjective<TestSuiteChromosome> secondaryObjectiveInstance = null;
      switch (secondaryObjective) {
        case AVG_LENGTH:
          secondaryObjectiveInstance = new MinimizeAverageLengthSecondaryObjective();
          break;
        case MAX_LENGTH:
          secondaryObjectiveInstance = new MinimizeMaxLengthSecondaryObjective();
          break;
        case TOTAL_LENGTH:
          secondaryObjectiveInstance = new MinimizeTotalLengthSecondaryObjective();
          break;
        case EXCEPTIONS:
          secondaryObjectiveInstance = new MinimizeExceptionsSecondaryObjective();
          break;
        case SIZE:
          secondaryObjectiveInstance = new MinimizeSizeSecondaryObjective();
          break;
        case IBRANCH:
          secondaryObjectiveInstance = new IBranchSecondaryObjective();
          break;
        case RHO:
          secondaryObjectiveInstance = new RhoTestSuiteSecondaryObjective();
          break;
        default:
          throw new RuntimeException(
              "ERROR: asked for unknown secondary objective \"" + secondaryObjective.name() + "\"");
      }
      TestSuiteChromosome.addSecondaryObjective(secondaryObjectiveInstance);
    }
  }
}
