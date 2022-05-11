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
package org.smartut.runtime.testdata;

import org.smartut.runtime.RuntimeSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * When using a mocked environment, new methods will be added to the test cluster to
 * manipulate the environment. Those methods might take data as input (eg String).
 * However, we might want to put constraints on what data is used, and how it is
 * manipulated by the search operators (eg mutation operators).
 *
 * Created by arcuri on 12/11/14.
 */
public class EnvironmentDataList {

    public static List<Class<?>> getListOfClasses(){
        List<Class<?>> classes = new ArrayList<>();

        if(RuntimeSettings.useVFS) {
            classes.add(SmartUtFile.class);
        }

        if(RuntimeSettings.useVNET){
            classes.add(SmartUtLocalAddress.class);
            classes.add(SmartUtRemoteAddress.class);
            classes.add(SmartUtURL.class);
        }

        return classes;
    }

}
