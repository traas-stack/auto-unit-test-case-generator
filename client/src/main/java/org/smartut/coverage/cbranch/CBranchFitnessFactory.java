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

package org.smartut.coverage.cbranch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smartut.coverage.branch.BranchCoverageFactory;
import org.smartut.coverage.branch.BranchCoverageTestFitness;
import org.smartut.setup.CallContext;
import org.smartut.setup.DependencyAnalysis;
import org.smartut.setup.callgraph.CallGraph;
import org.smartut.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gordon Fraser, mattia
 * 
 */
public class CBranchFitnessFactory extends AbstractFitnessFactory<CBranchTestFitness> {

	private static final Logger logger = LoggerFactory.getLogger(CBranchFitnessFactory.class);

	/* (non-Javadoc)
	 * @see org.smartut.coverage.TestFitnessFactory#getCoverageGoals()
	 */
	@Override
	public List<CBranchTestFitness> getCoverageGoals() {
		//TODO this creates duplicate goals. Momentary fixed using a Set, but it should be optimised
		Set<CBranchTestFitness> goals = new HashSet<>();

		// retrieve set of branches
		BranchCoverageFactory branchFactory = new BranchCoverageFactory();
		List<BranchCoverageTestFitness> branchGoals = branchFactory.getCoverageGoals();
		CallGraph callGraph = DependencyAnalysis.getCallGraph();

		// try to find all occurrences of this branch in the call tree
		for (BranchCoverageTestFitness branchGoal : branchGoals) {
			logger.info("Adding context branches for " + branchGoal.toString());
			for (CallContext context : callGraph.getMethodEntryPoint(branchGoal.getClassName(),
				                          branchGoal.getMethod())) {
				goals.add(new CBranchTestFitness(branchGoal.getBranchGoal(), context));
			}
		} 
		
		logger.info("Created " + goals.size() + " goals");
		return new ArrayList<>(goals);
	}
}

