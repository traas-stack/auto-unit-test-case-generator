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
package org.smartut.ga.stoppingconditions;

import org.smartut.ga.Chromosome;
import org.smartut.ga.metaheuristics.SearchListener;
import org.smartut.utils.PublicCloneable;

public interface StoppingCondition<T extends Chromosome<T>> extends SearchListener<T>,
		PublicCloneable<StoppingCondition<T>> {

	/**
	 * Force a specific amount of used up budget. Handle with care!
	 *
	 * @param value
	 *            The new amount of used up budget for this StoppingCondition
	 */
    void forceCurrentValue(long value);

	/**
	 * How much of the budget have we used up
	 *
	 * @return a long.
	 */
    long getCurrentValue();

	/**
	 * Get upper limit of resources
	 *
	 * Mainly used for toString()
	 *
	 * @return limit
	 */
    long getLimit();

	/**
	 * <p>isFinished</p>
	 *
	 * @return a boolean.
	 */
	boolean isFinished();

	/**
	 * Reset everything
	 */
	void reset();

	/**
	 * Set new upper limit of resources
	 *
	 * @param limit a long.
	 */
	void setLimit(long limit);
}
