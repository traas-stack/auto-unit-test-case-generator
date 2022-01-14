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
package org.smartut.coverage.exception;

import org.smartut.TestGenerationContext;
import org.smartut.coverage.MethodNameMatcher;
import org.smartut.coverage.branch.Branch;
import org.smartut.coverage.branch.BranchCoverageGoal;
import org.smartut.coverage.branch.BranchPool;
import org.smartut.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 03/04/2016.
 */
public class TryCatchCoverageFactory extends AbstractFitnessFactory<TryCatchCoverageTestFitness> {

    private static final Logger logger = LoggerFactory.getLogger(TryCatchCoverageFactory.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.smartut.coverage.TestCoverageFactory#getCoverageGoals()
	 */
    /** {@inheritDoc} */
    @Override
    public List<TryCatchCoverageTestFitness> getCoverageGoals() {
        List<TryCatchCoverageTestFitness> goals = new ArrayList<>();

        // logger.info("Getting branches");
        for (String className : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).knownClasses()) {
            final MethodNameMatcher matcher = new MethodNameMatcher();

            // Branches
            for (String methodName : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).knownMethods(className)) {
                if (!matcher.methodMatches(methodName)) {
                    logger.info("Method " + methodName + " does not match criteria. ");
                    continue;
                }

                for (Branch b : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).retrieveBranchesInMethod(className,
                        methodName)) {
                    if(b.isInstrumented()) {
                        goals.add(new TryCatchCoverageTestFitness(new BranchCoverageGoal(b,
                                true, b.getClassName(), b.getMethodName())));
                        if(!b.ignoreFalseBranch()) {
                            goals.add(new TryCatchCoverageTestFitness(new BranchCoverageGoal(b,
                                    false, b.getClassName(), b.getMethodName())));
                        }
                    }
                }
            }
        }
        return goals;
    }

}
