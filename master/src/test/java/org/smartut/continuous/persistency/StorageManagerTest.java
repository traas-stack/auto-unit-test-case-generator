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
package org.smartut.continuous.persistency;

import java.io.File;

import org.smartut.xsd.Project;
import org.junit.Assert;

import org.junit.Test;

public class StorageManagerTest {

	@Test
	public void testDefaultProjectInfo(){
		
		StorageManager sm = new StorageManager();
		sm.clean();
		
		try{
			Project project = StorageManager.getDatabaseProject();
			Assert.assertNotNull(project);
		} finally {
			sm.clean();
		}
	}
	
	
	@Test
	public void extractClassNameTest(){
		String z = File.separator;
		String base = z+"some"+z+"thing"+z;
		String packageName = "foo";
		String className = "boiade";
		String full = base+packageName+z+className+".java";
		
		StorageManager storage = new StorageManager();
		String result = storage.extractClassName(new File(base), new File(full));
				
		Assert.assertEquals(packageName+"."+className, result);
	}
}
