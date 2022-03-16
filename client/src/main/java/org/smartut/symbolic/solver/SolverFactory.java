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
package org.smartut.symbolic.solver;

import org.smartut.Properties;
import org.smartut.symbolic.solver.avm.SmartUtSolver;
import org.smartut.symbolic.solver.cvc4.CVC4Solver;
import org.smartut.symbolic.solver.z3.Z3Solver;

public class SolverFactory {

	private static final SolverFactory instance = new SolverFactory();

	public static SolverFactory getInstance() {
		return instance;
	}

	public Solver buildNewSolver() {
		switch (Properties.DSE_SOLVER) {
		case Z3_SOLVER:
			return new Z3Solver(true);
		case CVC4_SOLVER: {
			CVC4Solver solver = new CVC4Solver(true);
			solver.setRewriteNonLinearConstraints(true);
			return solver;
		}
		case SMARTUT_SOLVER:
		default:
			return new SmartUtSolver();
		}
	}

}
