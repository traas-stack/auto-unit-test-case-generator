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


import org.smartut.runtime.testdata.SmartUtURL;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.Randomness;
import org.smartut.utils.StringUtil;

/**
 * Created by arcuri on 12/14/14.
 */
public class UrlPrimitiveStatement extends EnvironmentDataStatement<SmartUtURL> {

	private static final long serialVersionUID = 2062390100066807026L;

	public UrlPrimitiveStatement(TestCase tc) {
        this(tc, null);
        randomize();
    }


    public UrlPrimitiveStatement(TestCase tc, SmartUtURL value) {
        super(tc, SmartUtURL.class, value);
    }

    @Override
    public String getTestCode(String varName) {
        String testCode = "";
        VariableReference retval = getReturnValue();
        Object value = getValue();

        if (value != null) {
            String escapedURL = StringUtil.getEscapedString(((SmartUtURL) value).getUrl());
            testCode += ((Class<?>) retval.getType()).getSimpleName() + " "
                    + varName + " = new "
                    + ((Class<?>) retval.getType()).getSimpleName() + "(\""
                    + escapedURL + "\");\n";
        } else {
            testCode += ((Class<?>) retval.getType()).getSimpleName() + " "
                    + varName + " = null;\n";
        }
        return testCode;
    }

    @Override
    public void delta() {
        randomize();
    }

    @Override
    public void zero() {

    }

    @Override
    public void randomize() {
        String url = Randomness.choice(tc.getAccessedEnvironment().getViewOfRemoteURLs());
        if (url != null) {
            setValue(new SmartUtURL(url));
        } else {
            setValue(null);
        }
    }
}
