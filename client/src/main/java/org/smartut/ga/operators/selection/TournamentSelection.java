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
package org.smartut.ga.operators.selection;

import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.Chromosome;
import org.smartut.utils.Randomness;


/**
 * Select an individual from a population as winner of a number of tournaments
 *
 * @author Gordon Fraser
 */
public class TournamentSelection<T extends Chromosome<T>> extends SelectionFunction<T> {

	private static final long serialVersionUID = -7465418404056357932L;

	public TournamentSelection() {
	}

	public TournamentSelection(TournamentSelection<?> other) {
		// empty copy constructor
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
			T selected = population.get(new_num);

			if (maximize) {
				if (selected.getFitness() > population.get(winner).getFitness()) {
					winner = new_num;
				}
			} else {
				if (selected.getFitness() < population.get(winner).getFitness()) {
					winner = new_num;
				}
			}
			round++;
		}

		return winner;
	}

}
