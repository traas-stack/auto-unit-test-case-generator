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
package org.smartut;

/**
 * Package information should never be hardcoded, as packages can be refactored or shaded.
 * All access should be through reflection or .class variables
 *
 * Created by Andrea Arcuri on 22/10/15.
 */
public class PackageInfo {

    private static final String SHADED = "shaded"; //WARN: do not modify it, as it is used in xml files as well

    public static String getSmartUtPackage(){
        return PackageInfo.class.getPackage().getName();
    }

    public static String getSmartUtPackageWithSlash(){
        return getSmartUtPackage().replace('.', '/');
    }

    /**
     * The package were third-party libraries are shaded into
     * @return
     */
    public static String getShadedPackageForThirdPartyLibraries(){
        return getSmartUtPackage() + "." +SHADED;
    }

    public static String getNameWithSlash(Class<?> klass){
        return klass.getName().replace('.', '/');
    }


    /**
     * The package name of the shaded SmartUt. Used only for when testing SmartUt
     * @return
     */
    public static String getShadedSmartUtPackage(){
        String shaded = SHADED + ".";
        String smartut = getSmartUtPackage();
        if(smartut.startsWith(shaded)){
            return smartut;
        } else {
            return shaded + smartut;
        }
    }


    public static boolean isCurrentlyShaded(){
        return getSmartUtPackage().startsWith(SHADED);
    }
}
