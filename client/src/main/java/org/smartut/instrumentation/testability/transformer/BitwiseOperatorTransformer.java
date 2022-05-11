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

import org.smartut.instrumentation.testability.BooleanHelper;
import org.smartut.instrumentation.testability.BooleanTestabilityTransformation;
import org.smartut.instrumentation.TransformationStatistics;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Make sure bitwise operations on transformed Booleans are still valid
 */
public class BitwiseOperatorTransformer extends MethodNodeTransformer {

	private final BooleanTestabilityTransformation booleanTestabilityTransformation;

	/**
	 * @param booleanTestabilityTransformation
	 */
	public BitwiseOperatorTransformer(
			BooleanTestabilityTransformation booleanTestabilityTransformation) {
		this.booleanTestabilityTransformation = booleanTestabilityTransformation;
	}

	/* (non-Javadoc)
	 * @see org.smartut.instrumentation.MethodNodeTransformer#transformInsnNode(org.objectweb.asm.tree.MethodNode, org.objectweb.asm.tree.InsnNode)
	 */
	@Override
	protected AbstractInsnNode transformInsnNode(MethodNode mn, InsnNode insnNode) {
		if (insnNode.getOpcode() == Opcodes.IOR
		        || insnNode.getOpcode() == Opcodes.IAND
		        || insnNode.getOpcode() == Opcodes.IXOR) {

			if (this.booleanTestabilityTransformation.isBooleanOnStack(mn, insnNode, 0)
			        && this.booleanTestabilityTransformation.isBooleanOnStack(mn, insnNode, 1)) {
				if (insnNode.getOpcode() == Opcodes.IOR) {
					MethodInsnNode push = new MethodInsnNode(Opcodes.INVOKESTATIC,
					        Type.getInternalName(BooleanHelper.class), "IOR",
					        Type.getMethodDescriptor(Type.INT_TYPE, new Type[] {
					                Type.INT_TYPE, Type.INT_TYPE }));
					mn.instructions.insertBefore(insnNode, push);
					mn.instructions.remove(insnNode);
					TransformationStatistics.transformedBitwise();
					return push;
				} else if (insnNode.getOpcode() == Opcodes.IAND) {
					MethodInsnNode push = new MethodInsnNode(Opcodes.INVOKESTATIC,
					        Type.getInternalName(BooleanHelper.class), "IAND",
					        Type.getMethodDescriptor(Type.INT_TYPE, new Type[] {
					                Type.INT_TYPE, Type.INT_TYPE }));
					mn.instructions.insertBefore(insnNode, push);
					mn.instructions.remove(insnNode);
					TransformationStatistics.transformedBitwise();
					return push;

				} else if (insnNode.getOpcode() == Opcodes.IXOR) {
					MethodInsnNode push = new MethodInsnNode(Opcodes.INVOKESTATIC,
					        Type.getInternalName(BooleanHelper.class), "IXOR",
					        Type.getMethodDescriptor(Type.INT_TYPE, new Type[] {
					                Type.INT_TYPE, Type.INT_TYPE }));
					mn.instructions.insertBefore(insnNode, push);
					mn.instructions.remove(insnNode);
					TransformationStatistics.transformedBitwise();
					return push;
				}
			}
		}
		return insnNode;
	}
}