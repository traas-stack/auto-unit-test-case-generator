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
package org.smartut.symbolic.expr;

import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.token.NewTokenizerExpr;
import org.smartut.symbolic.expr.token.StringNextTokenExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StringNextTokenExprTest {

    @Test
    public void testEquals() {
        StringConstant helloWorldStr = new StringConstant("Hello World");
        StringConstant delimiterStr = new StringConstant(" ");
        NewTokenizerExpr newTokenizerExpr = new NewTokenizerExpr(helloWorldStr, delimiterStr);
        StringNextTokenExpr left = new StringNextTokenExpr(newTokenizerExpr, helloWorldStr.getConcreteValue());
        StringNextTokenExpr right = new StringNextTokenExpr(newTokenizerExpr, helloWorldStr.getConcreteValue());
        assertEquals(left, right);
    }

    @Test
    public void testNotEquals() {
        StringConstant helloWorldStr = new StringConstant("Hello World");
        StringConstant goodByeWorldStr = new StringConstant("Goodbye World");
        StringConstant delimiterStr = new StringConstant(" ");
        NewTokenizerExpr leftNewTokenizerExpr = new NewTokenizerExpr(helloWorldStr, delimiterStr);
        StringNextTokenExpr left = new StringNextTokenExpr(leftNewTokenizerExpr, helloWorldStr.getConcreteValue());

        NewTokenizerExpr rightNewTokenizerExpr = new NewTokenizerExpr(goodByeWorldStr, delimiterStr);
        StringNextTokenExpr right = new StringNextTokenExpr(rightNewTokenizerExpr, helloWorldStr.getConcreteValue());
        assertNotEquals(left, right);
    }
}
