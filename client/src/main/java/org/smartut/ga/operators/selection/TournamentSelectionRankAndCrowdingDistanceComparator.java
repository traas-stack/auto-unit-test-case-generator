/*
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
package org.smartut.ga.operators.selection;

import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.Chromosome;
import org.smartut.ga.comparators.RankAndCrowdingDistanceComparator;
import org.smartut.utils.Randomness;

/**
 * Select an individual from a population as winner of a number of tournaments according to the
 * "non-dominance" relationship and the crowding distance.
 *
 * @author Annibale Panichella, Fitsum M. Kifetew
 */
public class TournamentSelectionRankAndCrowdingDistanceComparator<T extends Chromosome<T>>
		extends SelectionFunction<T> {

	private static final long serialVersionUID = 781669365989544671L;

	private final RankAndCrowdingDistanceComparator<T> comparator;

	public TournamentSelectionRankAndCrowdingDistanceComparator() {
		this.comparator = new RankAndCrowdingDistanceComparator<>(this.maximize);
	}

	public TournamentSelectionRankAndCrowdingDistanceComparator(boolean isToMaximize) {
		this.comparator = new RankAndCrowdingDistanceComparator<>(isToMaximize);
	}

	public TournamentSelectionRankAndCrowdingDistanceComparator(
			TournamentSelectionRankAndCrowdingDistanceComparator<?> other) {
		this.comparator = new RankAndCrowdingDistanceComparator<>(other.comparator);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Perform the tournament on the population, return one index
	 */
	@Override
	public int getIndex(List<T> population) {
		int new_num = Randomness.nextInt(population.size());
		int winner = new_num;

		int round = 0;

		while (round < Properties.TOURNAMENT_SIZE - 1) {
			new_num = Randomness.nextInt(population.size());
			if (new_num == winner)
				new_num = (new_num+1) % population.size();
			T selected = population.get(new_num);
			int flag = comparator.compare(selected, population.get(winner));
			if (flag < 0) {
				winner = new_num;
			} 
			round++;
		}

		return winner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T select(List<T> population) {
		return population.get(getIndex(population));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximize(boolean max) {
		super.setMaximize(max);
		this.comparator.setMaximize(max);
	}
}
