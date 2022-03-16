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
package org.smartut.graphs.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;

public class BytecodeInstructionFactory {

	/**
	 * <p>
	 * createBytecodeInstruction
	 * </p>
	 * 
	 * @param className
	 *            a {@link java.lang.String} object.
	 * @param methodName
	 *            a {@link java.lang.String} object.
	 * @param instructionId
	 *            a int.
	 * @param bytecodeOffset
	 *            a int.
	 * @param node
	 *            a {@link org.objectweb.asm.tree.AbstractInsnNode} object.
	 * @return a {@link org.smartut.graphs.cfg.BytecodeInstruction} object.
	 */
	public static BytecodeInstruction createBytecodeInstruction(ClassLoader classLoader,
	        String className, String methodName, int instructionId, int bytecodeOffset,
	        AbstractInsnNode node) {

		BytecodeInstruction instruction = new BytecodeInstruction(classLoader, className,
		        methodName, instructionId, bytecodeOffset, node);

		return instruction;
	}

}
