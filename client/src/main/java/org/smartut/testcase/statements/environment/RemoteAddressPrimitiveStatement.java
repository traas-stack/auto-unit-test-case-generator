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
package org.smartut.testcase.statements.environment;

import org.smartut.runtime.testdata.SmartUtAddress;
import org.smartut.runtime.testdata.SmartUtRemoteAddress;
import org.smartut.runtime.vnet.EndPointInfo;
import org.smartut.seeding.ConstantPool;
import org.smartut.seeding.ConstantPoolManager;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.Randomness;
import org.smartut.utils.StringUtil;

/**
 * Created by arcuri on 12/17/14.
 */
public class RemoteAddressPrimitiveStatement extends EnvironmentDataStatement<SmartUtRemoteAddress>{

	private static final long serialVersionUID = -4863601663573415059L;

	public RemoteAddressPrimitiveStatement(TestCase tc) {
        this(tc, null);
        randomize();
    }

    public RemoteAddressPrimitiveStatement(TestCase tc, SmartUtRemoteAddress value) {
        super(tc, SmartUtRemoteAddress.class, value);
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
        SmartUtRemoteAddress addr;

        double threshold = 0.8; //TODO parameter
        boolean accessed = Randomness.nextDouble() <= threshold;

        if(accessed && !tc.getAccessedEnvironment().getViewOfRemoteContactedPorts().isEmpty()){
            // use an address that the SUT tried to contact
            EndPointInfo info = Randomness.choice(tc.getAccessedEnvironment().getViewOfRemoteContactedPorts());
            String host = info.getHost();
            int port = info.getPort();//TODO check why it can be a 0 here
            port = getPort(port);
            addr = new SmartUtRemoteAddress(host,port);
        } else {
            /*
                make up an address based on string/int constants.
                this is needed to handle the cases when the SUT get
                an incoming message, and then check its remote address.

                TODO: here we could validate if host/port values are
                indeed valid. However, as this is kind of special case,
                and likely not so common, it doesn't have high priority.
             */
            ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
            String host = constantPool.getRandomString();
            int port = constantPool.getRandomInt();
            port = getPort(port);
            addr = new SmartUtRemoteAddress(host,port);
        }

        setValue(addr);
    }

    private int getPort(int port) {
        if(port<=0 || port > 65535){
            port = 12345; //just a valid port number
        }
        return port;
    }

}
