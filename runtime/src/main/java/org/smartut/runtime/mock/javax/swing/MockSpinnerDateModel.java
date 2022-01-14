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
package org.smartut.runtime.mock.javax.swing;

import org.smartut.runtime.mock.OverrideMock;
import org.smartut.runtime.mock.java.util.MockCalendar;
import org.smartut.runtime.mock.java.util.MockDate;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gordon on 01/02/2016.
 */
public class MockSpinnerDateModel extends SpinnerDateModel implements OverrideMock {

    private static final long serialVersionUID = -7236911608512230647L;

    public MockSpinnerDateModel(Date value, Comparable start, Comparable end, int calendarField) {
        super(value, start, end, calendarField);
        this.setValue(MockCalendar.getInstance());
    }

    public MockSpinnerDateModel() {
        this(new MockDate(), null, null, Calendar.DAY_OF_MONTH);
    }

    public Object getNextValue() {
        Calendar cal = MockCalendar.getInstance();
        cal.setTime((Date) getValue());

        cal.add(getCalendarField(), 1);
        Date next = cal.getTime();

        return ((getEnd() == null) || (getEnd().compareTo(next) >= 0)) ? next : null;
    }

    public Object getPreviousValue() {
        Calendar cal = MockCalendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(getCalendarField(), -1);
        Date prev = cal.getTime();
        return ((getStart() == null) || (getStart() .compareTo(prev) <= 0)) ? prev : null;
    }



}
