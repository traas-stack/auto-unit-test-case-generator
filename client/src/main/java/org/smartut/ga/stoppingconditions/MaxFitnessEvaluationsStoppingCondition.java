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
package org.smartut.ga.stoppingconditions;

import org.smartut.Properties;
import org.smartut.ga.Chromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stop search after a predefined maximum search depth
 * 
 * @author Gordon Fraser
 */
public class MaxFitnessEvaluationsStoppingCondition<T extends Chromosome<T>> extends StoppingConditionImpl<T> {

	private static final Logger logger = LoggerFactory.getLogger(MaxFitnessEvaluationsStoppingCondition.class);

	private static final long serialVersionUID = 208241490252275613L;

	/** Maximum number of evaluations */
	protected long maxEvaluations;

	/** Maximum number of iterations */
	protected static long currentEvaluation = 0;

	public MaxFitnessEvaluationsStoppingCondition() {
		maxEvaluations = Properties.SEARCH_BUDGET;
	}

	public MaxFitnessEvaluationsStoppingCondition(MaxFitnessEvaluationsStoppingCondition<?> that) {
		this.maxEvaluations = that.maxEvaluations;
	}

	@Override
	public MaxFitnessEvaluationsStoppingCondition<T> clone() {
		return new MaxFitnessEvaluationsStoppingCondition<>(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Stop when maximum number of fitness evaluations has been reached
	 */
	@Override
	public boolean isFinished() {
		logger.info("Current number of fitness_evaluations: " + currentEvaluation);
		return currentEvaluation >= maxEvaluations;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Keep track of the number of fitness evaluations
	 */
	@Override
	public void fitnessEvaluation(T individual) {
		currentEvaluation++;
	}

	/**
	 * Static getter method
	 * 
	 * @return a long.
	 */
	public static long getNumFitnessEvaluations() {
		return currentEvaluation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * At the end, reset
	 */
	@Override
	public void reset() {
		currentEvaluation = 0;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.StoppingCondition#setLimit(int)
	 */
	/** {@inheritDoc} */
	@Override
	public void setLimit(long limit) {
		maxEvaluations = limit;
	}

	/** {@inheritDoc} */
	@Override
	public long getLimit() {
		return maxEvaluations;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.StoppingCondition#getCurrentValue()
	 */
	/** {@inheritDoc} */
	@Override
	public long getCurrentValue() {
		return currentEvaluation;
	}

	/** {@inheritDoc} */
	@Override
	public void forceCurrentValue(long value) {
		currentEvaluation = value;
	}

}
