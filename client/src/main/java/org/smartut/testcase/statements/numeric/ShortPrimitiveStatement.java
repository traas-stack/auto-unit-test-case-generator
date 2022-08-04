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

package org.smartut.testcase.statements.numeric;

import org.smartut.Properties;
import org.smartut.seeding.ConstantPool;
import org.smartut.seeding.ConstantPoolManager;
import org.smartut.testcase.TestCase;
import org.smartut.utils.Randomness;

/**
 * <p>
 * ShortPrimitiveStatement class.
 * </p>
 * 
 * @author fraser
 */
public class ShortPrimitiveStatement extends NumericalPrimitiveStatement<Short> {

	private static final long serialVersionUID = -1041008456902695964L;

	/**
	 * <p>
	 * Constructor for ShortPrimitiveStatement.
	 * </p>
	 * 
	 * @param tc
	 *            a {@link org.smartut.testcase.TestCase} object.
	 * @param value
	 *            a {@link java.lang.Short} object.
	 */
	public ShortPrimitiveStatement(TestCase tc, Short value) {
		super(tc, short.class, value);
	}

	/**
	 * <p>
	 * Constructor for ShortPrimitiveStatement.
	 * </p>
	 * 
	 * @param tc
	 *            a {@link org.smartut.testcase.TestCase} object.
	 */
	public ShortPrimitiveStatement(TestCase tc) {
		super(tc, short.class, (short) 0);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#zero()
	 */
	/** {@inheritDoc} */
	@Override
	public void zero() {
		value = (short) 0;
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#delta()
	 */
	/** {@inheritDoc} */
	@Override
	public void delta() {
		short delta = (short)Math.floor(Randomness.nextGaussian() * Properties.MAX_DELTA);
		value = (short) (value + delta);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#increment(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void increment(long delta) {
		value = (short) (value + (short) delta);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#randomize()
	 */
	/** {@inheritDoc} */
	@Override
	public void randomize() {
		short max = (short) Math.min(Properties.MAX_INT, 32767);
		if (Randomness.nextDouble() >= Properties.PRIMITIVE_POOL) {
			value = (short) ((Randomness.nextGaussian() * max));
		}
		else {
			ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
			value = (short) constantPool.getRandomInt();
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#increment()
	 */
	/** {@inheritDoc} */
	@Override
	public void increment() {
		increment((short) 1);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.NumericalPrimitiveStatement#setMid(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void setMid(Short min, Short max) {
		value = (short) (min + ((max - min) / 2));
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.NumericalPrimitiveStatement#decrement()
	 */
	/** {@inheritDoc} */
	@Override
	public void decrement() {
		increment(-1);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.NumericalPrimitiveStatement#isPositive()
	 */
	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return value >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public void negate() {
		value = (short) -value;
	}
}
