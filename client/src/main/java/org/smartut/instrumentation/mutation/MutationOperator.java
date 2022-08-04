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

package org.smartut.instrumentation.mutation;

import java.util.List;

import org.smartut.coverage.mutation.Mutation;
import org.smartut.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;


/**
 * <p>MutationOperator interface.</p>
 *
 * @author Gordon Fraser
 */
public interface MutationOperator {

	/**
	 * Insert the mutation into the bytecode
	 *
	 * @param mn a {@link org.objectweb.asm.tree.MethodNode} object.
	 * @param className a {@link java.lang.String} object.
	 * @param methodName a {@link java.lang.String} object.
	 * @param instruction a {@link org.smartut.graphs.cfg.BytecodeInstruction} object.
	 * @param frame a {@link org.objectweb.asm.tree.analysis.Frame} object.
	 * @return a {@link java.util.List} object.
	 */
    List<Mutation> apply(MethodNode mn, String className, String methodName,
                         BytecodeInstruction instruction, Frame frame);

	/**
	 * Check if the mutation operator is applicable to the instruction
	 *
	 * @param instruction a {@link org.smartut.graphs.cfg.BytecodeInstruction} object.
	 * @return a boolean.
	 */
    boolean isApplicable(BytecodeInstruction instruction);

}
