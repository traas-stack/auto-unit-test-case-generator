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
package org.smartut.runtime.mock.java.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

public class InetAddressTest {

	@Test
	public void testGetByName() throws UnknownHostException{
		
		String googleAddr = "www.google.com";
		String smartutAddr = "www.smartut.org";
		
		InetAddress google = MockInetAddress.getByName(googleAddr);
		InetAddress smartut = MockInetAddress.getByName(smartutAddr);
		
		Assert.assertEquals(googleAddr, google.getHostName());
		Assert.assertEquals(smartutAddr, smartut.getHostName());
		
		Assert.assertNotEquals(google.getHostAddress(), smartut.getHostAddress());
	}
}
