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
package org.smartut.coverage.ambiguity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smartut.coverage.line.LineCoverageTestFitness;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testsuite.TestSuiteChromosome;
import org.smartut.testsuite.TestSuiteFitnessFunction;

/**
 * 
 * @author José Campos
 */
public class AmbiguityCoverageSuiteFitness extends TestSuiteFitnessFunction {

	private static final long serialVersionUID = -2721073655092419390L;

	
	private final Set<Integer> goals;

	
	public AmbiguityCoverageSuiteFitness() {

		this.goals = new LinkedHashSet<>();
		for (LineCoverageTestFitness goal : AmbiguityCoverageFactory.getGoals()) {
			this.goals.add(goal.getLine());
		}
	}

	@Override
	public double getFitness(TestSuiteChromosome suite) {

		List<StringBuilder> transposedMatrix = new ArrayList<>(AmbiguityCoverageFactory.getTransposedMatrix());
		List<Set<Integer>> coveredLines = new ArrayList<>();

		// Execute test cases and collect the covered lines
		List<ExecutionResult> results = runTestSuite(suite);
		for (ExecutionResult result : results) {
			coveredLines.add(result.getTrace().getCoveredLines());
		}

		Map<String, Integer> groups = new HashMap<>();
		int g_i = 0;

		for (Integer goal : this.goals) {
			StringBuffer str = null;

			if (transposedMatrix.size() > g_i) {
				str = new StringBuffer(transposedMatrix.get(g_i).length() + coveredLines.size());
				str.append(transposedMatrix.get(g_i));
			} else {
				str = new StringBuffer(coveredLines.size());
			}

			for (Set<Integer> covered : coveredLines) {
				str.append( covered.contains(goal) ? "1" : "0" );
			}

			if (!groups.containsKey(str.toString())) {
				groups.put(str.toString(), 1); // in the beginning they are ambiguity, so they belong to the same group '1'
			} else {
				groups.put(str.toString(), groups.get(str.toString()) + 1);
			}

			g_i++;
		}

		//double fitness = AmbiguityCoverageFactory.getAmbiguity(this.goals.size(), groups) * 1.0 / AmbiguityCoverageFactory.getMaxAmbiguityScore();
		double fitness = TestFitnessFunction.normalize(AmbiguityCoverageFactory.getAmbiguity(this.goals.size(), groups));
		updateIndividual(suite, fitness);

		return fitness;
	}
}
