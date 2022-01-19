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
package org.smartut.ga.comparators;

import static org.junit.Assert.assertEquals;

import org.smartut.ga.NSGAChromosome;
import org.junit.Test;

/**
 * 
 * @author José Campos
 */
public class TestStrengthFitnessComparator {

  @Test
  public void testNull() {
    StrengthFitnessComparator comparator = new StrengthFitnessComparator();
    assertEquals(0, comparator.compare(null, null));
    assertEquals(-1, comparator.compare(new NSGAChromosome(), null));
    assertEquals(1, comparator.compare(null, new NSGAChromosome()));
  }

  @Test
  public void testEquals() {
    StrengthFitnessComparator comparator = new StrengthFitnessComparator();
    // compare chromosomes by the default strength value, 0.0
    assertEquals(0, comparator.compare(new NSGAChromosome(), new NSGAChromosome()));

    // testing a custom value
    NSGAChromosome c1 = new NSGAChromosome();
    c1.setDistance(0.6);
    NSGAChromosome c2 = new NSGAChromosome();
    c2.setDistance(0.6);
    assertEquals(0, comparator.compare(c1, c2));
  }

  @Test
  public void testStrength() {
    StrengthFitnessComparator comparator = new StrengthFitnessComparator();

    NSGAChromosome c1 = new NSGAChromosome();
    NSGAChromosome c2 = new NSGAChromosome();

    c1.setDistance(0.1);
    c2.setDistance(0.9);
    assertEquals(-1, comparator.compare(c1, c2)); // c1 dominates c2

    c1.setDistance(0.9);
    c2.setDistance(0.1);
    assertEquals(1, comparator.compare(c1, c2)); // c2 dominates c1
  }
}
