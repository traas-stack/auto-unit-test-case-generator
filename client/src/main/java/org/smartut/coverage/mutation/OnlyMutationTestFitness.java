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
package org.smartut.coverage.mutation;

import org.smartut.Properties;
import org.smartut.ga.archive.Archive;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.execution.ExecutionResult;

/**
 * 
 * @author gordon
 *
 */
public class OnlyMutationTestFitness extends MutationTestFitness {

	private static final long serialVersionUID = -6724941216935595963L;

	public OnlyMutationTestFitness(Mutation m) {
		super(m);
	}
	
	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		double fitness = 0.0;

		// Get control flow distance
		if (!result.getTrace().wasMutationTouched(mutation.getId()) || result.calledReflection()) {
			fitness = 1.0;
		} else {
			fitness = normalize(result.getTrace().getMutationDistance(mutation.getId()));
			logger.debug("Infection distance for mutation = " + fitness);
		}

		updateIndividual(individual, fitness);

		if (fitness == 0.0) {
			individual.getTestCase().addCoveredGoal(this);
		}

		if (Properties.TEST_ARCHIVE) {
			Archive.getArchiveInstance().updateArchive(this, individual, fitness);
		}

		return fitness;	
	}


	@Override
	public String getTargetMethod() {
		return "Weak " + mutation.toString();
	}

}
