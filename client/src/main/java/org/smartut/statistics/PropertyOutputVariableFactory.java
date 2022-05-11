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
package org.smartut.statistics;

import org.smartut.Properties;

/**
 * Output variable that represents a value stored in the properties
 * 
 * @author gordon
 *
 */
public class PropertyOutputVariableFactory {

	private String propertyName;
	
	public PropertyOutputVariableFactory(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public OutputVariable<String> getVariable() {
		try {
			return new OutputVariable<>(propertyName, Properties.getStringValue(propertyName));
		} catch (Exception e) {
			// TODO: What would be better?
			return new OutputVariable<>(propertyName, "error");
		}
	}

}
