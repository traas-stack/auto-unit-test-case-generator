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

import java.util.Map;
import java.util.HashMap;

import org.jgrapht.ext.ComponentAttributeProvider;
public class CCFGEdgeAttributeProvider implements ComponentAttributeProvider<CCFGEdge> {

	
	/** {@inheritDoc} */
	@Override
	public Map<String, String> getComponentAttributes(CCFGEdge edge) {
		Map<String, String> r = new HashMap<>();
		if(edge instanceof CCFGFrameEdge) {
			r.put("style", "dotted");
		} else if(edge instanceof CCFGMethodCallEdge) {
			r.put("style","bold");
		}
		return r;
	}

}
