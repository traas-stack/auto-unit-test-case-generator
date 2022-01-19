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

package org.smartut.eclipse.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.smartut.utils.ArrayUtil;

/**
 * @author Gordon Fraser
 * 
 */
public class MethodExtractingVisitor extends ASTVisitor {

	public String result = "";
	
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		String[] specialMethods = {"initSmartUtFramework",
				   "initializeClasses",
				   "resetClasses",
				   "setSystemProperties",
				   "clearSmartUtFramework"};
		if (! ArrayUtil.contains(specialMethods, node.getName().toString())) {
			// System.out.println("Listing method to add:\n"+node.toString());
			result += node.toString();
			methods.add(node);
		}
		return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
}
