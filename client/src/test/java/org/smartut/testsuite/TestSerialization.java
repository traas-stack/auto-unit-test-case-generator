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
package org.smartut.testsuite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.junit.Assert;
import org.junit.Test;

public class TestSerialization {

	@Test
	public void testSerializationNonEmptySuite() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		double fitness = 0.9950513142057124d;
		TestSuiteChromosome chromosome = new TestSuiteChromosome();
		TestChromosome testChromosome = new TestChromosome();
		TestCase test = new DefaultTestCase();
		PrimitiveStatement<?> statement = PrimitiveStatement.getPrimitiveStatement(test, int.class);
		test.addStatement(statement);
		testChromosome.setTestCase(test);
		testChromosome.setFitness(null, 3.14d);		
		chromosome.setFitness(null, fitness);
		chromosome.setCoverage(null, 0.5);
		chromosome.updateAge(24);
		chromosome.setChanged(true);
		chromosome.addTest(testChromosome);
		oos.writeObject(chromosome);
		byte [] baSerialized = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(baSerialized);
		ObjectInputStream ois = new ObjectInputStream(bais);
		TestSuiteChromosome copy = (TestSuiteChromosome) ois.readObject();
		Assert.assertEquals(chromosome.getFitness(), copy.getFitness(), 0.0);
		Assert.assertEquals(chromosome.getAge(), copy.getAge());
		Assert.assertEquals(chromosome.getCoverage(), copy.getCoverage(), 0.0);
		Assert.assertEquals(chromosome.getCoveredGoals(), copy.getCoveredGoals());
		Assert.assertEquals(chromosome.isChanged(), copy.isChanged());
		
		Assert.assertEquals(chromosome.getTestChromosome(0).getFitness(), copy.getTestChromosome(0).getFitness(), 0.0);
	}
}
