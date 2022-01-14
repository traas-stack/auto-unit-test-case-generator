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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.smartut.SmartUt;
import org.smartut.Properties;
import org.smartut.SystemTestBase;
import org.smartut.Properties.Criterion;
import org.smartut.statistics.OutputVariable;
import org.smartut.statistics.SearchStatistics;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.Euclidean;

/**
 * @author José Campos
 */
public class CoverageAnalysisOfSmartUtTestSuiteSystemTest extends SystemTestBase {

	@Test
	public void testOneFitnessFunction() {
		SmartUt smartut = new SmartUt();

		String targetClass = Euclidean.class.getCanonicalName();
		Properties.TARGET_CLASS = targetClass;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		String[] command = new String[] {
			"-class", targetClass,
			"-Djunit=" + targetClass + Properties.JUNIT_SUFFIX,
			"-measureCoverage"
		};

		SearchStatistics statistics = (SearchStatistics) smartut.parseCommandLine(command);
		Assert.assertNotNull(statistics);

		Map<String, OutputVariable<?>> data = statistics.getOutputVariables();
		assertEquals(5, (Integer) data.get("Total_Goals").getValue(), 0.0);
		assertEquals(5, (Integer) data.get("Covered_Goals").getValue(), 0.0);
	}
}
