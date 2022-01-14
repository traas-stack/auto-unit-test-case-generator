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
package org.smartut.ga.metaheuristics.mosa.structural;

import org.smartut.testcase.TestFitnessFunction;
import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * 
 * @author Annibale Panichella
 */
public class DependencyEdge extends DefaultEdge {

	private static final long serialVersionUID = 1L;
	
	//private static final Logger logger = LoggerFactory.getLogger(BranchFitnessGraph.class);

	public TestFitnessFunction getSource() {
		return (TestFitnessFunction) super.getSource();
	}

	public TestFitnessFunction getTarget() {
		return (TestFitnessFunction) super.getTarget();
	}

}
