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
package org.smartut.runtime.mock.java.time;

import org.smartut.runtime.mock.StaticReplacementMock;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Created by gordon on 24/01/2016.
 */
public class MockYear implements StaticReplacementMock {
    @Override
    public String getMockedClassName() {
        return Year.class.getName();
    }

    public static Year now() {
        return now(MockClock.systemDefaultZone());
    }

    public static Year now(ZoneId zone) {
        return now(MockClock.system(zone));
    }

    public static Year now(Clock clock) {
        return Year.now(clock);
    }

    public static Year of(int isoYear) {
        return Year.of(isoYear);
    }

    public static Year from(TemporalAccessor temporal) {
        return Year.from(temporal);
    }

    public static Year parse(CharSequence text) {
        return Year.parse(text);
    }

    public static Year parse(CharSequence text, DateTimeFormatter formatter) {
        return Year.parse(text, formatter);
    }

    public static boolean isLeap(long year) {
        return Year.isLeap(year);
    }

}
