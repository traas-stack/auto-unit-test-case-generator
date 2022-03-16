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
package org.smartut.ga.archive;

import org.smartut.Properties;
import org.smartut.coverage.branch.BranchCoverageTestFitness;
import org.smartut.coverage.branch.OnlyBranchCoverageTestFitness;
import org.smartut.coverage.cbranch.CBranchTestFitness;
import org.smartut.coverage.dataflow.AllDefsCoverageTestFitness;
import org.smartut.coverage.dataflow.DefUseCoverageTestFitness;
import org.smartut.coverage.exception.ExceptionCoverageTestFitness;
import org.smartut.coverage.exception.TryCatchCoverageTestFitness;
import org.smartut.coverage.ibranch.IBranchTestFitness;
import org.smartut.coverage.io.input.InputCoverageTestFitness;
import org.smartut.coverage.io.output.OutputCoverageTestFitness;
import org.smartut.coverage.line.LineCoverageTestFitness;
import org.smartut.coverage.method.MethodCoverageTestFitness;
import org.smartut.coverage.method.MethodNoExceptionCoverageTestFitness;
import org.smartut.coverage.method.MethodTraceCoverageTestFitness;
import org.smartut.coverage.mutation.MutationTestFitness;
import org.smartut.coverage.mutation.OnlyMutationTestFitness;
import org.smartut.coverage.mutation.StrongMutationTestFitness;
import org.smartut.coverage.mutation.WeakMutationTestFitness;
import org.smartut.coverage.rho.RhoCoverageTestFitness;
import org.smartut.coverage.statement.StatementCoverageTestFitness;
import org.smartut.ga.FitnessFunction;
import org.smartut.runtime.util.AtMostOnceLogger;
import org.smartut.testcase.TestChromosome;
import org.smartut.utils.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArchiveUtils {

  private static final Logger logger = LoggerFactory.getLogger(ArchiveUtils.class);

  /**
   * Checks whether a specific goal (i.e., a {@link org.smartut.testcase.TestFitnessFunction}
   * object) is of an enabled criterion. A criterion is considered enabled if and only if defined in
   * {@link org.smartut.Properties.CRITERION}.
   * 
   * @param goal a {@link org.smartut.testcase.TestFitnessFunction} object
   * @return true if criterion of goal is enabled, false otherwise
   */
  public static boolean isCriterionEnabled(FitnessFunction<TestChromosome> goal) {
    for (Properties.Criterion criterion : Properties.CRITERION) {
      switch (criterion) {
        case EXCEPTION:
          if (goal instanceof ExceptionCoverageTestFitness) {
            return true;
          }
          break;
        case DEFUSE:
          if (goal instanceof DefUseCoverageTestFitness) {
            return true;
          }
          break;
        case ALLDEFS:
          if (goal instanceof AllDefsCoverageTestFitness) {
            return true;
          }
          break;
        case BRANCH:
          if (goal instanceof BranchCoverageTestFitness) {
            return true;
          }
          break;
        case CBRANCH:
          if (goal instanceof CBranchTestFitness) {
            return true;
          }
          break;
        case STRONGMUTATION:
          if (goal instanceof StrongMutationTestFitness) {
            return true;
          }
          break;
        case WEAKMUTATION:
          if (goal instanceof WeakMutationTestFitness) {
            return true;
          }
          break;
        case MUTATION:
          if (goal instanceof MutationTestFitness) {
            return true;
          }
          break;
        case STATEMENT:
          if (goal instanceof StatementCoverageTestFitness) {
            return true;
          }
          break;
        case RHO:
          if (goal instanceof RhoCoverageTestFitness) {
            return true;
          }
          break;
        case AMBIGUITY:
          if (goal instanceof LineCoverageTestFitness) {
            return true;
          }
          break;
        case IBRANCH:
          if (goal instanceof IBranchTestFitness) {
            return true;
          }
          break;
        case READABILITY:
          break;
        case ONLYBRANCH:
          if (goal instanceof OnlyBranchCoverageTestFitness) {
            return true;
          }
          break;
        case ONLYMUTATION:
          if (goal instanceof OnlyMutationTestFitness) {
            return true;
          }
          break;
        case METHODTRACE:
          if (goal instanceof MethodTraceCoverageTestFitness) {
            return true;
          }
          break;
        case METHOD:
          if (goal instanceof MethodCoverageTestFitness) {
            return true;
          }
          break;
        case METHODNOEXCEPTION:
          if (goal instanceof MethodNoExceptionCoverageTestFitness) {
            return true;
          }
          break;
        case LINE:
          if (goal instanceof LineCoverageTestFitness) {
            return true;
          }
          break;
        case ONLYLINE:
          if (goal instanceof LineCoverageTestFitness) {
            return true;
          }
          break;
        case OUTPUT:
          if (goal instanceof OutputCoverageTestFitness) {
            return true;
          }
          break;
        case INPUT:
          if (goal instanceof InputCoverageTestFitness) {
            return true;
          }
          break;
        case TRYCATCH:
          if (goal instanceof TryCatchCoverageTestFitness) {
            return true;
          }
          break;
        default:
          AtMostOnceLogger.warn(logger, "Unknown criterion '" + criterion.name() + "'");
          break;
      }
    }
    if (ArrayUtil.contains(Properties.SECONDARY_OBJECTIVE, Properties.SecondaryObjective.IBRANCH)) {
      if (goal instanceof IBranchTestFitness) {
        return true;
      }
    }

    return false;
  }
}
