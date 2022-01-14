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

package org.smartut.coverage.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>PrimePathPool class.</p>
 *
 * @author Gordon Fraser
 */
public class PrimePathPool {

	// maps: className -> methodName  -> DUVarName -> branchID -> List of Definitions in that branch 
	/** Constant <code>primePathMap</code> */
	public static Map<String, Map<String, List<PrimePath>>> primePathMap = new HashMap<String, Map<String, List<PrimePath>>>();

	/** Constant <code>primePathCounter=0</code> */
	public static int primePathCounter = 0;

	/**
	 * <p>getSize</p>
	 *
	 * @return a int.
	 */
	public static int getSize() {
		return primePathCounter;
	}

	/**
	 * <p>add</p>
	 *
	 * @param path a {@link org.smartut.coverage.path.PrimePath} object.
	 */
	public static void add(PrimePath path) {
		String className = path.className;
		String methodName = path.methodName;

		if (!primePathMap.containsKey(className))
			primePathMap.put(className, new HashMap<String, List<PrimePath>>());
		if (!primePathMap.get(className).containsKey(methodName))
			primePathMap.get(className).put(methodName, new ArrayList<PrimePath>());
		path.condensate();
		primePathMap.get(className).get(methodName).add(path);
		primePathCounter++;
	}
}
