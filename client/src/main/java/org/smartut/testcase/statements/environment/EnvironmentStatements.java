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
package org.smartut.testcase.statements.environment;

import org.smartut.runtime.testdata.*;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.smartut.utils.Randomness;

/**
 * @see org.smartut.runtime.testdata.EnvironmentDataList
 *
 * Created by arcuri on 12/11/14.
 */
public class EnvironmentStatements {

    public static boolean isEnvironmentData(Class<?> clazz){
        for(Class<?> env : EnvironmentDataList.getListOfClasses()){
            if(clazz.equals(env)){
                return true;
            }
        }
        return false;
    }

    public static PrimitiveStatement<?> getStatement(Class<?> clazz, TestCase tc) throws IllegalArgumentException{
        if(!isEnvironmentData(clazz)){
            throw new IllegalArgumentException("Class "+clazz.getName()+" is not an environment data type");
        }

        if(clazz.equals(SmartUtFile.class)){
            return new FileNamePrimitiveStatement(tc, new SmartUtFile(Randomness.choice(tc.getAccessedEnvironment().getViewOfAccessedFiles())));
        } else if(clazz.equals(SmartUtLocalAddress.class)){
            return new LocalAddressPrimitiveStatement(tc);
        } else if(clazz.equals(SmartUtRemoteAddress.class)){
            return new RemoteAddressPrimitiveStatement(tc);
        } else if(clazz.equals(SmartUtURL.class)){
            return new UrlPrimitiveStatement(tc);
        }

        throw new RuntimeException("SmartUt bug: unhandled class "+clazz.getName());
    }
}
