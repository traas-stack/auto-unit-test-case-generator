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
package org.smartut.statistics.backend;

import java.util.LinkedHashMap;
import java.util.Map;

import org.smartut.ga.Chromosome;
import org.smartut.statistics.OutputVariable;

/**
 * Backend to be used only for helping writing test cases
 * 
 * @author arcuri
 *
 */
public class DebugStatisticsBackend  extends ConsoleStatisticsBackend{

	private static Map<String, OutputVariable<?>> latestWritten;
	
	@Override
	public void writeData(Chromosome<?> result, Map<String, OutputVariable<?>> data) {
		super.writeData(result, data);
		latestWritten = new LinkedHashMap<>();
		latestWritten.putAll(data);
	}

	public static Map<String, OutputVariable<?>> getLatestWritten() {
		return latestWritten;
	}

}
