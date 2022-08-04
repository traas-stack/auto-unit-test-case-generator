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
package org.smartut.symbolic.solver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.ExecuteException;
import org.smartut.utils.ProcessLauncher;
import org.smartut.utils.ProcessTimeoutException;

public abstract class SmtSolver extends Solver {

	public SmtSolver(boolean addMissingVariables) {
		super(addMissingVariables);
	}

	public SmtSolver() {
		super();
	}

	/**
	 * 
	 * @param solverCmd
	 * @param smtQueryStr
	 * @param hardTimeout
	 * @return
	 * @throws IOException
	 * @throws SolverTimeoutException
	 * @throws SolverErrorException
	 */
	protected static void launchNewSolvingProcess(String solverCmd, String smtQueryStr, int hardTimeout, OutputStream stdout)
			throws IOException, SolverTimeoutException, SolverErrorException {

		ByteArrayInputStream input = new ByteArrayInputStream(smtQueryStr.getBytes());

		ProcessLauncher launcher = new ProcessLauncher(stdout, input);

		long solver_start_time_millis = System.currentTimeMillis();
		try {
			int exit_code = launcher.launchNewProcess(solverCmd, hardTimeout);
			
			if (exit_code == 0) {
				logger.debug("Solver execution finished normally");
				return;
			} else {
				String errMsg = String.format("Solver execution finished abnormally with exit code {}", exit_code);
				logger.debug(errMsg);
				throw new SolverErrorException(errMsg);
			}
		} catch (ExecuteException ex) {
			logger.debug("Solver subprocesses failed");
			throw new SolverErrorException("Solver subprocesses failed");
			
		} catch (ProcessTimeoutException ex) {
			logger.debug("Solver stopped due to solver timeout");
			throw new SolverTimeoutException();
			
		} finally {
			long solver_end_time_millis = System.currentTimeMillis();
			long solver_duration_secs = (solver_end_time_millis - solver_start_time_millis) / 1000;
			logger.debug("Solver execution time was {}s", solver_duration_secs);
		}

	}

}
