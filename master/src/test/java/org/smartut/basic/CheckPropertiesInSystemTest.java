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
package org.smartut.basic;

import org.smartut.SystemTestBase;
import org.junit.Assert;

import org.junit.Test;

public class CheckPropertiesInSystemTest extends SystemTestBase {

	private static final String PROPERTY = "Some_property_name_used_for_testing_SystemTest";
	
	@Test
	public void setProperty(){
		Assert.assertNull(System.getProperty(PROPERTY));
		System.setProperty(PROPERTY, PROPERTY);
		Assert.assertNotNull(System.getProperty(PROPERTY));		
	}
	
	@Test
	public void getProperty(){
		Assert.assertNull(System.getProperty(PROPERTY));
	}
}
