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

import org.smartut.runtime.testdata.SmartUtAddress;
import org.smartut.runtime.testdata.SmartUtLocalAddress;
import org.smartut.runtime.vnet.EndPointInfo;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.Randomness;
import org.smartut.utils.StringUtil;

/**
 * Created by arcuri on 12/15/14.
 */
public class LocalAddressPrimitiveStatement extends EnvironmentDataStatement<SmartUtLocalAddress> {

	private static final long serialVersionUID = -6687351650507282638L;

	public LocalAddressPrimitiveStatement(TestCase tc) {
        this(tc,null);
        randomize();
    }

    public LocalAddressPrimitiveStatement(TestCase tc, SmartUtLocalAddress value) {
        super(tc, SmartUtLocalAddress.class, value);
    }

    @Override
    public String getTestCode(String varName) {
        String testCode = "";
        VariableReference retval = getReturnValue();
        Object value = getValue();

        if (value != null) {
            String escapedAddress = StringUtil.getEscapedString(((SmartUtAddress) value).getHost());
            int port = ((SmartUtAddress) value).getPort();

            testCode += ((Class<?>) retval.getType()).getSimpleName() + " "
                    + varName + " = new "
                    + ((Class<?>) retval.getType()).getSimpleName() + "(\""
                    + escapedAddress +"\", "+port + ");\n";
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

        SmartUtLocalAddress addr;

        if(!tc.getAccessedEnvironment().getViewOfLocalListeningPorts().isEmpty()){
            EndPointInfo info = Randomness.choice(tc.getAccessedEnvironment().getViewOfLocalListeningPorts());
            String host = info.getHost();
            int port = info.getPort();
            addr = new SmartUtLocalAddress(host,port);
        } else {
            /*
                no point in creating local addresses that the SUT has
                never accessed
             */
            addr = null;
        }

        setValue(addr);
    }
}
