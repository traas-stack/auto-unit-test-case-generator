package org.smartut.symbolic.solver.z3;

import org.smartut.symbolic.solver.Solver;
import org.smartut.symbolic.solver.TestSolverIsInteger;

public class TestZ3IsInteger extends TestSolverIsInteger {

	@Override
	public Solver getSolver() {
		return new Z3Solver();
	}

}
