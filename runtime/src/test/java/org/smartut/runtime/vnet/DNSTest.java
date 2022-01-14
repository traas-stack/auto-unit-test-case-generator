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
package org.smartut.runtime.vnet;

import org.junit.Assert;

import org.junit.Test;

public class DNSTest {

	@Test
	public void testResolve(){
		
		DNS dns = new DNS();
		
		String lbAddr = "127.0.0.1";
		
		String lb = dns.resolve(lbAddr);
		String google = dns.resolve("www.google.com");
		String smartut = dns.resolve("www.smartut.org");
		
		Assert.assertEquals(lbAddr,lb);
		
		Assert.assertNotNull(google);
		Assert.assertNotNull(smartut);
		
		Assert.assertNotEquals(lb,google);
		Assert.assertNotEquals(lb,smartut);
		Assert.assertNotEquals(google,smartut);
	}
}
