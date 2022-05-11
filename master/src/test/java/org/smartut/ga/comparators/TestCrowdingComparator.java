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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.smartut.ga.NSGAChromosome;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author José Campos
 */
public class TestCrowdingComparator
{
    @Test
    public void testCrowdingComparisonOperatorMinimize()
    {
        NSGAChromosome c1 = new NSGAChromosome();
        NSGAChromosome c2 = new NSGAChromosome();
        NSGAChromosome c3 = new NSGAChromosome();

        // Set Rank
        c1.setRank(1);
        c2.setRank(0);
        c3.setRank(0);

        // Set Distance
        c1.setDistance(0.1);
        c2.setDistance(0.5);
        c3.setDistance(0.4);

        List<NSGAChromosome> population = new ArrayList<>();
        population.add(c1);
        population.add(c2);
        population.add(c3);

        RankAndCrowdingDistanceComparator<NSGAChromosome> cc =
                new RankAndCrowdingDistanceComparator<>(false);
        population.sort(cc);

        // assert by Rank
        Assert.assertEquals(0, population.get(0).getRank());
        Assert.assertEquals(0, population.get(1).getRank());
        Assert.assertEquals(1, population.get(2).getRank());

        // assert by Distance
        Assert.assertEquals(0.5, population.get(0).getDistance(), 0.0);
        Assert.assertEquals(0.4, population.get(1).getDistance(), 0.0);
        Assert.assertEquals(0.1, population.get(2).getDistance(), 0.0);
    }

    @Test
    public void testCrowdingComparisonOperatorMaximize()
    {
        NSGAChromosome c1 = new NSGAChromosome();
        NSGAChromosome c2 = new NSGAChromosome();
        NSGAChromosome c3 = new NSGAChromosome();

        // Set Rank
        c1.setRank(1);
        c2.setRank(0);
        c3.setRank(0);

        // Set Distance
        c1.setDistance(0.1);
        c2.setDistance(0.5);
        c3.setDistance(0.4);

        List<NSGAChromosome> population = new ArrayList<>();
        population.add(c1);
        population.add(c2);
        population.add(c3);

        RankAndCrowdingDistanceComparator<NSGAChromosome> cc =
                new RankAndCrowdingDistanceComparator<>(true);
        population.sort(cc);

        // assert by Rank
        Assert.assertEquals(1, population.get(0).getRank());
        Assert.assertEquals(0, population.get(1).getRank());
        Assert.assertEquals(0, population.get(2).getRank());

        // assert by Distance
        Assert.assertEquals(0.1, population.get(0).getDistance(), 0.0);
        Assert.assertEquals(0.5, population.get(1).getDistance(), 0.0);
        Assert.assertEquals(0.4, population.get(2).getDistance(), 0.0);
    }
}
