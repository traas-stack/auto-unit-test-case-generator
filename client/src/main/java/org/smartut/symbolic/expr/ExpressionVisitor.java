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

import org.smartut.symbolic.expr.bv.IntegerBinaryExpression;
import org.smartut.symbolic.expr.bv.IntegerComparison;
import org.smartut.symbolic.expr.bv.IntegerConstant;
import org.smartut.symbolic.expr.bv.IntegerUnaryExpression;
import org.smartut.symbolic.expr.bv.IntegerVariable;
import org.smartut.symbolic.expr.bv.RealComparison;
import org.smartut.symbolic.expr.bv.RealToIntegerCast;
import org.smartut.symbolic.expr.bv.RealUnaryToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringBinaryComparison;
import org.smartut.symbolic.expr.bv.StringBinaryToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringMultipleComparison;
import org.smartut.symbolic.expr.bv.StringMultipleToIntegerExpression;
import org.smartut.symbolic.expr.bv.StringToIntegerCast;
import org.smartut.symbolic.expr.bv.StringUnaryToIntegerExpression;
import org.smartut.symbolic.expr.fp.IntegerToRealCast;
import org.smartut.symbolic.expr.fp.RealBinaryExpression;
import org.smartut.symbolic.expr.fp.RealConstant;
import org.smartut.symbolic.expr.fp.RealUnaryExpression;
import org.smartut.symbolic.expr.fp.RealVariable;
import org.smartut.symbolic.expr.reader.StringReaderExpr;
import org.smartut.symbolic.expr.ref.GetFieldExpression;
import org.smartut.symbolic.expr.ref.ReferenceConstant;
import org.smartut.symbolic.expr.ref.ReferenceVariable;
import org.smartut.symbolic.expr.str.IntegerToStringCast;
import org.smartut.symbolic.expr.str.RealToStringCast;
import org.smartut.symbolic.expr.str.StringBinaryExpression;
import org.smartut.symbolic.expr.str.StringConstant;
import org.smartut.symbolic.expr.str.StringMultipleExpression;
import org.smartut.symbolic.expr.str.StringUnaryExpression;
import org.smartut.symbolic.expr.str.StringVariable;
import org.smartut.symbolic.expr.token.HasMoreTokensExpr;
import org.smartut.symbolic.expr.token.NewTokenizerExpr;
import org.smartut.symbolic.expr.token.NextTokenizerExpr;
import org.smartut.symbolic.expr.token.StringNextTokenExpr;

public interface ExpressionVisitor<K, V> {

	K visit(IntegerBinaryExpression n, V arg) ;

	K visit(IntegerComparison n, V arg) ;

	K visit(IntegerConstant n, V arg) ;

	K visit(IntegerUnaryExpression n, V arg) ;

	K visit(IntegerVariable n, V arg) ;

	K visit(RealComparison n, V arg) ;

	K visit(RealToIntegerCast n, V arg) ;

	K visit(RealUnaryToIntegerExpression n, V arg) ;

	K visit(StringBinaryComparison n, V arg) ;

	K visit(StringBinaryToIntegerExpression n, V arg) ;

	K visit(StringMultipleComparison n, V arg) ;

	K visit(StringMultipleToIntegerExpression n, V arg) ;

	K visit(StringToIntegerCast n, V arg) ;

	K visit(StringUnaryToIntegerExpression n, V arg) ;

	K visit(IntegerToRealCast n, V arg) ;

	K visit(RealBinaryExpression n, V arg) ;

	K visit(RealConstant n, V arg) ;

	K visit(RealUnaryExpression n, V arg) ;

	K visit(RealVariable n, V arg) ;

	K visit(StringReaderExpr n, V arg) ;

	K visit(IntegerToStringCast n, V arg) ;

	K visit(RealToStringCast n, V arg) ;

	K visit(StringBinaryExpression n, V arg) ;

	K visit(StringConstant n, V arg) ;

	K visit(StringMultipleExpression n, V arg) ;

	K visit(StringUnaryExpression n, V arg) ;

	K visit(StringVariable n, V arg) ;

	K visit(HasMoreTokensExpr n, V arg) ;

	K visit(NewTokenizerExpr n, V arg) ;

	K visit(NextTokenizerExpr n, V arg) ;

	K visit(StringNextTokenExpr n, V arg);

	K visit(ReferenceConstant r, V arg);

	K visit(ReferenceVariable r, V arg);

	K visit(GetFieldExpression r, V arg);

}
