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
package org.smartut.ga.comparators;

import org.smartut.ga.FitnessFunction;
import org.smartut.ga.NSGAChromosome;
import org.smartut.ga.problems.Problem;
import org.smartut.ga.problems.singleobjective.Booths;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author José Campos
 */
public class TestSortByFitness
{
    @Test
    public void testSortByFitnessC1win()
    {
        Problem<NSGAChromosome> p = new Booths();
        List<FitnessFunction<NSGAChromosome>> fitnessFunctions = p.getFitnessFunctions();
        FitnessFunction<NSGAChromosome> ff = fitnessFunctions.get(0);

        NSGAChromosome c1 = new NSGAChromosome();
        NSGAChromosome c2 = new NSGAChromosome();

        // Set Fitness
        c1.setFitness(ff, 0.7);
        c2.setFitness(ff, 0.3);

        List<NSGAChromosome> population = new ArrayList<>();
        population.add(c1);
        population.add(c2);

        population.sort(new SortByFitness<>(ff, true));

        Assert.assertSame(population.get(0), c1);
        Assert.assertEquals(population.get(0).getFitness(ff), 0.7, 0.0);

        Assert.assertSame(population.get(1), c2);
        Assert.assertEquals(population.get(1).getFitness(ff), 0.3, 0.0);
    }

    @Test
    public void testSortByFitnessC2win()
    {
        Problem<NSGAChromosome> p = new Booths();
        List<FitnessFunction<NSGAChromosome>> fitnessFunctions = p.getFitnessFunctions();
        FitnessFunction<NSGAChromosome> ff = fitnessFunctions.get(0);

        NSGAChromosome c1 = new NSGAChromosome();
        NSGAChromosome c2 = new NSGAChromosome();

        // Set Fitness
        c1.setFitness(ff, 0.3);
        c2.setFitness(ff, 0.7);

        List<NSGAChromosome> population = new ArrayList<>();
        population.add(c1);
        population.add(c2);

        population.sort(new SortByFitness<>(ff, true));

        Assert.assertSame(population.get(0), c2);
        Assert.assertEquals(population.get(0).getFitness(ff), 0.7, 0.0);

        Assert.assertEquals(population.get(1).getFitness(ff), 0.3, 0.0);
        Assert.assertSame(population.get(1), c1);
    }

    @Test
    public void testSortByFitnessEqual()
    {
        Problem<NSGAChromosome> p = new Booths();
        List<FitnessFunction<NSGAChromosome>> fitnessFunctions = p.getFitnessFunctions();
        FitnessFunction<NSGAChromosome> ff = fitnessFunctions.get(0);

        NSGAChromosome c1 = new NSGAChromosome();
        NSGAChromosome c2 = new NSGAChromosome();

        // Set Fitness
        c1.setFitness(ff, 0.5);
        c2.setFitness(ff, 0.5);

        List<NSGAChromosome> population = new ArrayList<>();
        population.add(c1);
        population.add(c2);

        population.sort(new SortByFitness<>(ff, true));

        Assert.assertSame(population.get(0), c1);
        Assert.assertEquals(population.get(0).getFitness(ff), 0.5, 0.0);

        Assert.assertEquals(population.get(1).getFitness(ff), 0.5, 0.0);
        Assert.assertSame(population.get(1), c2);
    }
}
