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

import org.smartut.ga.Chromosome;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;

/**
 * Stop the search when the fitness has reached 0 (assuming minimization)
 *
 * @author Gordon Fraser
 */
public class ZeroFitnessStoppingCondition<T extends Chromosome<T>> extends StoppingConditionImpl<T> {

	private static final long serialVersionUID = -6925872054053635256L;

	/** Keep track of lowest fitness seen so far */
	private double lastFitness;

	public ZeroFitnessStoppingCondition() {
		lastFitness = Double.MAX_VALUE;
	}

	public ZeroFitnessStoppingCondition(ZeroFitnessStoppingCondition<?> that) {
		this.lastFitness = that.lastFitness;
	}

	@Override
	public ZeroFitnessStoppingCondition<T> clone() {
		return new ZeroFitnessStoppingCondition<>(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Update information on currently lowest fitness
	 */
	@Override
	public void iteration(GeneticAlgorithm<T> algorithm) {
		lastFitness = Math.min(lastFitness, algorithm.getBestIndividual().getFitness());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns true if best individual has fitness <= 0.0
	 */
	@Override
	public boolean isFinished() {
		return lastFitness <= 0.0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Reset currently observed best fitness
	 */
	@Override
	public void reset() {
		lastFitness = Double.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.StoppingCondition#setLimit(int)
	 */
	/** {@inheritDoc} */
	@Override
	public void setLimit(long limit) {
		// Do nothing
	}

	/** {@inheritDoc} */
	@Override
	public long getLimit() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.smartut.ga.StoppingCondition#getCurrentValue()
	 */
	/** {@inheritDoc} */
	@Override
	public long getCurrentValue() {
		return (long) (lastFitness + 0.5); // TODO: Why +0.5??
	}

	/**
	 * <p>setFinished</p>
	 */
	public void setFinished() {
		lastFitness = 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public void forceCurrentValue(long value) {
		// TODO Auto-generated method stub
		// TODO ?
	}

}
