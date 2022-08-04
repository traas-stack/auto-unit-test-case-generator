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
package org.smartut.instrumentation.testability.transformer;

import org.smartut.instrumentation.BooleanArrayInterpreter;
import org.smartut.instrumentation.testability.BooleanTestabilityTransformation;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Frame;

/**
 * Make sure array accesses of boolean arrays are also transformed
 */
public class BooleanArrayIndexTransformer extends MethodNodeTransformer {
	private final Frame[] frames;

	// TODO: Use currentFrames
	public BooleanArrayIndexTransformer(Frame[] frames) {
		this.frames = frames;
	}

	/* (non-Javadoc)
	 * @see org.smartut.instrumentation.MethodNodeTransformer#transformInsnNode(org.objectweb.asm.tree.MethodNode, org.objectweb.asm.tree.InsnNode)
	 */
	@Override
	protected AbstractInsnNode transformInsnNode(MethodNode mn, InsnNode insnNode) {
		if (frames == null) {
			return insnNode;
		}

		if (insnNode.getOpcode() == Opcodes.BALOAD) {
			Frame current = frames[mn.instructions.indexOf(insnNode)];
			int size = current.getStackSize();
			if (current.getStack(size - 2) == BooleanArrayInterpreter.INT_ARRAY) {
				BooleanTestabilityTransformation.logger.info("Array is of boolean type, changing BALOAD to IALOAD");
				InsnNode replacement = new InsnNode(Opcodes.IALOAD);
				mn.instructions.insertBefore(insnNode, replacement);
				mn.instructions.remove(insnNode);
				return replacement;
			}
		} else if (insnNode.getOpcode() == Opcodes.BASTORE) {
			Frame current = frames[mn.instructions.indexOf(insnNode)];
			int size = current.getStackSize();
			if (current.getStack(size - 3) == BooleanArrayInterpreter.INT_ARRAY) {
				BooleanTestabilityTransformation.logger.info("Array is of boolean type, changing BASTORE to IASTORE");
				InsnNode replacement = new InsnNode(Opcodes.IASTORE);
				mn.instructions.insertBefore(insnNode, replacement);
				mn.instructions.remove(insnNode);
				return replacement;
			}
		}
		return insnNode;
	}

	/* (non-Javadoc)
	 * @see org.smartut.instrumentation.MethodNodeTransformer#transformTypeInsnNode(org.objectweb.asm.tree.MethodNode, org.objectweb.asm.tree.TypeInsnNode)
	 */
	@Override
	protected AbstractInsnNode transformTypeInsnNode(MethodNode mn,
	        TypeInsnNode typeNode) {
		if (frames == null)
			return typeNode;

		if (typeNode.getOpcode() == Opcodes.CHECKCAST) {
			Frame current = frames[mn.instructions.indexOf(typeNode)];
			int size = current.getStackSize();
			if (current.getStack(size - 1) == BooleanArrayInterpreter.INT_ARRAY) {
				BooleanTestabilityTransformation.logger.info("Array is of boolean type, changing CHECKCAST to [I");
				TypeInsnNode replacement = new TypeInsnNode(Opcodes.CHECKCAST, "[I");
				mn.instructions.insertBefore(typeNode, replacement);
				mn.instructions.remove(typeNode);
				return replacement;
			}
		}
		return typeNode;
	}
}