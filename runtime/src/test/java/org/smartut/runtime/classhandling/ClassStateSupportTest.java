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
package org.smartut.runtime.classhandling;

import org.junit.Assert;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.instrumentation.SmartUtClassLoader;
import org.junit.Test;

/**
 * Created by arcuri on 1/20/15.
 */
public class ClassStateSupportTest {

    @Test
    public void testInitializeClasses(){


        SmartUtClassLoader loader = new SmartUtClassLoader();
        String className = "com.examples.with.different.packagename.classhandling.TimeA";
        //no mocking
        RuntimeSettings.deactivateAllMocking();
        boolean problem = ClassStateSupport.initializeClasses(loader, className);
        Assert.assertFalse(problem);

        //with mocking
        RuntimeSettings.mockJVMNonDeterminism = true;
        className = "com.examples.with.different.packagename.classhandling.TimeB";
        problem = ClassStateSupport.initializeClasses(loader,className);
        Assert.assertFalse(problem);
    }
}
