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

package org.smartut.junit;

import java.util.List;
import java.util.Map;

import org.smartut.Properties;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestCodeVisitor;

/**
 * <p>
 * JUnit4TestAdapter class.
 * </p>
 * 
 * @author fraser
 */
public class JUnit4TestAdapter implements UnitTestAdapter {

	private String getJUnitTestShortName() {
		if (Properties.ECLIPSE_PLUGIN) {
			String res = "";
			if(Properties.TARGET_CLASS.equals("SmartUtTest"))
				res = org.smartut.annotations.SmartUtTest.class.getName();
			else
				res = "SmartUtTest";
			res += " (checked = false)";
			return res;
		} else {
			if(Properties.TARGET_CLASS.equals("Test"))
				return "org.junit.Test";
			else
				return "Test";
		}
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getImports()
	 */
	/** {@inheritDoc} */
	@Override
	public String getImports() {
		String imports = "";
		if ((Properties.ECLIPSE_PLUGIN) && (!Properties.TARGET_CLASS.equals("SmartUtTest")))
			imports += "import "+org.smartut.annotations.SmartUtTest.class.getName()+";\n";
		if(!Properties.TARGET_CLASS.equals("Test"))
			imports += "import org.junit.Test;\n";
		imports += "import static org.junit.Assert.*;\n";
		
		return imports;
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getClassDefinition(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public String getClassDefinition(String testName) {
		return "public class " + testName;
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getMethodDefinition(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public String getMethodDefinition(String testName) {
		StringBuilder builder = new StringBuilder();
		builder.append("  @" + getJUnitTestShortName() );
		//TODO remove once JUnit is fixed. See comments in Scaffolding regarding Timeout rule
		builder.append("(timeout = " + (Properties.TIMEOUT + 1000) + ")");
		builder.append("\n");
		builder.append("  public void " + testName + "() ");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getSuite(java.util.List)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSuite(List<String> suites) {
		StringBuilder builder = new StringBuilder();
		builder.append("import org.junit.runner.RunWith;\n");
		builder.append("import org.junit.runners.Suite;\n\n");

		for (String suite : suites) {
			if (suite.contains(".")) {
				builder.append("import ");
				builder.append(suite);
				builder.append(";\n");
			}
		}
		builder.append("\n");

		builder.append("@RunWith(Suite.class)\n");
		builder.append("@Suite.SuiteClasses({\n");
		boolean first = true;
		for (String suite : suites) {
			if (!first) {
				builder.append(",\n");
			}
			first = false;
			builder.append("  ");
			builder.append(suite.substring(suite.lastIndexOf(".") + 1));
			builder.append(".class");
		}
		builder.append("})\n");

		builder.append(getClassDefinition("GeneratedTestSuite"));
		builder.append(" {\n");
		builder.append("}\n");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getTestString(org.smartut.testcase.TestCase, java.util.Map)
	 */
	/** {@inheritDoc} */
	@Override
	public String getTestString(int id, TestCase test, Map<Integer, Throwable> exceptions) {
		return test.toCode(exceptions);
	}

	/* (non-Javadoc)
	 * @see org.smartut.junit.UnitTestAdapter#getTestString(int, org.smartut.testcase.TestCase, java.util.Map, org.smartut.testcase.TestCodeVisitor)
	 */
	/** {@inheritDoc} */
	@Override
	public String getTestString(int id, TestCase test,
	        Map<Integer, Throwable> exceptions, TestCodeVisitor visitor) {
		visitor.setExceptions(exceptions);
		test.accept(visitor);
		visitor.clearExceptions();
		return visitor.getCode();
	}
}
