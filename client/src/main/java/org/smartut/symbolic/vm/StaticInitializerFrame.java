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
package org.smartut.symbolic.vm;

import java.lang.reflect.Member;

import org.smartut.dse.MainConfig;

/**
 * Frame for a <clinit>() invocation
 * 
 * @author csallner@uta.edu (Christoph Csallner)
 */
final class StaticInitializerFrame extends Frame {

	private String className;

	/**
	 * Constructor
	 */
	StaticInitializerFrame(String className) {
		super(MainConfig.get().MAX_LOCALS_DEFAULT);
		this.className = className;
	}

	@Override
	public int getNrFormalParameters() {
		return 0;
	}

	@Override
	public int getNrFormalParametersTotal() {
		return 0;
	}

	@Override
	public Member getMember() {
		return null;
	}

	public Object getClassName() {
		return className;
	}
}
