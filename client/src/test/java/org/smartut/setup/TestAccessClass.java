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
package org.smartut.setup;

import com.examples.with.different.packagename.otherpackage.ExampleWithInnerClass;
import com.examples.with.different.packagename.otherpackage.ExampleWithStaticPackagePrivateInnerClass;
import org.smartut.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by gordon on 31/01/2016.
 */
public class TestAccessClass {

    @Test
    public void testPublicClass() {
        Properties.CLASS_PREFIX = "some.package";
        Properties.TARGET_CLASS = "some.package.Foo";
        boolean result = TestUsageChecker.canUse(ExampleWithStaticPackagePrivateInnerClass.class);
        Assert.assertTrue(result);
    }

    @Test
    public void testPublicInnerClass() {
        Properties.CLASS_PREFIX = "some.package";
        Properties.TARGET_CLASS = "some.package.Foo";
        boolean result = TestUsageChecker.canUse(ExampleWithInnerClass.Foo.class);
        Assert.assertTrue(result);
    }

    @Test
    public void testDefaultInnerClass() throws ClassNotFoundException {
        Properties.CLASS_PREFIX = "some.package";
        Properties.TARGET_CLASS = "some.package.Foo";
        Class<?> clazz = Class.forName("com.examples.with.different.packagename.otherpackage.ExampleWithStaticPackagePrivateInnerClass$Foo");
        boolean result = TestUsageChecker.canUse(clazz);
        Assert.assertFalse(result);
    }

    @Test
    public void testDefaultInnerClassInSamePackage() throws ClassNotFoundException {
        Properties.CLASS_PREFIX = "com.examples.with.different.packagename.otherpackage";
        Properties.TARGET_CLASS = "com.examples.with.different.packagename.otherpackage.ExampleWithStaticPackagePrivateInnerClass";
        Class<?> clazz = Class.forName("com.examples.with.different.packagename.otherpackage.ExampleWithStaticPackagePrivateInnerClass$Foo");
        boolean result = TestUsageChecker.canUse(clazz);
        Assert.assertTrue(result);
    }
}
