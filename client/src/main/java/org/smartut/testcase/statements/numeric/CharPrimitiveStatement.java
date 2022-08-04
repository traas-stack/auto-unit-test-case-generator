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
import org.smartut.testcase.TestCase;
import org.smartut.utils.Randomness;


/**
 * <p>CharPrimitiveStatement class.</p>
 *
 * @author fraser
 */
public class CharPrimitiveStatement extends NumericalPrimitiveStatement<Character> {

	private static final long serialVersionUID = -1960567565801078784L;

	/**
	 * <p>Constructor for CharPrimitiveStatement.</p>
	 *
	 * @param tc a {@link org.smartut.testcase.TestCase} object.
	 * @param value a {@link java.lang.Character} object.
	 */
	public CharPrimitiveStatement(TestCase tc, Character value) {
		super(tc, char.class, value);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>Constructor for CharPrimitiveStatement.</p>
	 *
	 * @param tc a {@link org.smartut.testcase.TestCase} object.
	 */
	public CharPrimitiveStatement(TestCase tc) {
		super(tc, char.class, (char) 0);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#zero()
	 */
	/** {@inheritDoc} */
	@Override
	public void zero() {
		value = (char) 0;
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#delta()
	 */
	/** {@inheritDoc} */
	@Override
	public void delta() {
		int delta = Randomness.nextInt(2 * Properties.MAX_DELTA) - Properties.MAX_DELTA;
		value = (char) (value + delta);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#increment(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void increment(long delta) {
		value = (char) (value + delta);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#randomize()
	 */
	/** {@inheritDoc} */
	@Override
	public void randomize() {
		value = Randomness.nextChar();
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.PrimitiveStatement#increment()
	 */
	/** {@inheritDoc} */
	@Override
	public void increment() {
		increment((char) 1);
	}

	/* (non-Javadoc)
	 * @see org.smartut.testcase.NumericalPrimitiveStatement#setMid(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void setMid(Character min, Character max) {
		value = (char) (min + ((max - min) / 2));
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
}
