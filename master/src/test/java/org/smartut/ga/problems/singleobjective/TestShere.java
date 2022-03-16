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
package org.smartut.ga.problems.singleobjective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smartut.Properties;
import org.smartut.ga.ChromosomeFactory;
import org.smartut.ga.FitnessFunction;
import org.smartut.ga.NSGAChromosome;
import org.smartut.ga.metaheuristics.GeneticAlgorithm;
import org.smartut.ga.metaheuristics.NSGAII;
import org.smartut.ga.metaheuristics.RandomFactory;
import org.smartut.ga.operators.crossover.SBXCrossover;
import org.smartut.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.smartut.ga.problems.Problem;
import org.smartut.ga.variables.DoubleVariable;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Comparator.comparingDouble;

public class TestShere
{
    @BeforeClass
    public static void setUp() {
        Properties.POPULATION = 100;
        Properties.SEARCH_BUDGET = 2500;
        Properties.CROSSOVER_RATE = 0.9;
        Properties.RANDOM_SEED = 1L;
    }

    @Test
    public void testSphereFitness()
    {
        Problem<NSGAChromosome> p = new Sphere();
        FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);

        double[] values = {-2.0};
        NSGAChromosome c = new NSGAChromosome(Math.pow(-10.0, 3.0), Math.pow(10.0, 3.0), values);
        Assert.assertEquals(((DoubleVariable) c.getVariables().get(0)).getValue(), -2.0, 0.0);

        Assert.assertEquals(f1.getFitness(c), 4.0, 0.0);
    }

    /**
     * Testing NSGA-II with Sphere Problem
     * 
     * @throws IOException 
     * @throws NumberFormatException 
     */
    @Test
    public void testSphere() throws NumberFormatException, IOException
    {
        Properties.MUTATION_RATE = 1d / 1d;

        ChromosomeFactory<NSGAChromosome> factory = new RandomFactory(false, 1, Math.pow(-10.0, 3.0),
                Math.pow(10.0, 3.0));

        GeneticAlgorithm<NSGAChromosome> ga = new NSGAII<>(factory);
        BinaryTournamentSelectionCrowdedComparison<NSGAChromosome> ts =
                new BinaryTournamentSelectionCrowdedComparison<>();
        //BinaryTournament ts = new BinaryTournament();
        ga.setSelectionFunction(ts);
        ga.setCrossOverFunction(new SBXCrossover());

        Problem<NSGAChromosome> p = new Sphere();
        final FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        ga.addFitnessFunction(f1);

        // execute
        ga.generateSolution();

        List<NSGAChromosome> chromosomes = new ArrayList<>(ga.getPopulation());
        chromosomes.sort(comparingDouble(chr -> chr.getFitness(f1)));

        for (NSGAChromosome chromosome : chromosomes)
            Assert.assertEquals(chromosome.getFitness(f1), 0.00, 0.01);

        for (NSGAChromosome chromosome : chromosomes) {
            DoubleVariable x = (DoubleVariable) chromosome.getVariables().get(0);
            System.out.printf("%f : %f\n", x.getValue(), chromosome.getFitness(f1));
        }
    }
}
