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
package org.smartut.graphs.ccfg;
public class CCFGFrameNode extends CCFGNode {

	private ClassControlFlowGraph.FrameNodeType type;
	
	/**
	 * <p>Constructor for CCFGFrameNode.</p>
	 *
	 * @param type a {@link org.smartut.graphs.ccfg.ClassControlFlowGraph.FrameNodeType} object.
	 */
	public CCFGFrameNode(ClassControlFlowGraph.FrameNodeType type) {
		this.type = type;
	}
	
	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link org.smartut.graphs.ccfg.ClassControlFlowGraph.FrameNodeType} object.
	 */
	public ClassControlFlowGraph.FrameNodeType getType() {
		return type;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Frame "+type.toString();
	}
	
}
