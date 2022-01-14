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
package org.smartut.ga.metaheuristics.mulambda;

import org.smartut.ga.Chromosome;
import org.smartut.ga.ChromosomeFactory;

/**
 * (1+1)EA
 *
 * @author Gordon Fraser
 */
public class OnePlusOneEA<T extends Chromosome<T>> extends MuPlusLambdaEA<T> {

	private static final long serialVersionUID = 5229089847512798127L;

	/**
	 * Constructor
	 *
	 * @param factory a {@link org.smartut.ga.ChromosomeFactory} object.
	 */
	public OnePlusOneEA(ChromosomeFactory<T> factory) {
		super(factory, 1, 1);
	}
}
