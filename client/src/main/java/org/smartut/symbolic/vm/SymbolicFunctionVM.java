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
package org.smartut.symbolic.vm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.smartut.symbolic.expr.IntegerConstraint;
import org.smartut.symbolic.expr.bv.IntegerValue;
import org.smartut.symbolic.expr.fp.RealValue;
import org.smartut.symbolic.expr.ref.ReferenceExpression;
import org.smartut.symbolic.vm.apache.regex.Perl5Matcher_Matches;
import org.smartut.symbolic.vm.bigint.BigInteger_Ctor;
import org.smartut.symbolic.vm.bigint.BigInteger_DivideAndRemainder;
import org.smartut.symbolic.vm.bigint.BigInteger_IntValue;
import org.smartut.symbolic.vm.math.ABS;
import org.smartut.symbolic.vm.math.ACOS;
import org.smartut.symbolic.vm.math.ASIN;
import org.smartut.symbolic.vm.math.ATAN;
import org.smartut.symbolic.vm.math.ATAN2;
import org.smartut.symbolic.vm.math.CBRT;
import org.smartut.symbolic.vm.math.CEIL;
import org.smartut.symbolic.vm.math.COS;
import org.smartut.symbolic.vm.math.COSH;
import org.smartut.symbolic.vm.math.CopySign;
import org.smartut.symbolic.vm.math.EXP;
import org.smartut.symbolic.vm.math.EXPM1;
import org.smartut.symbolic.vm.math.FLOOR;
import org.smartut.symbolic.vm.math.GetExponent;
import org.smartut.symbolic.vm.math.HYPOT;
import org.smartut.symbolic.vm.math.IEEEremainder;
import org.smartut.symbolic.vm.math.LOG;
import org.smartut.symbolic.vm.math.LOG10;
import org.smartut.symbolic.vm.math.LOG1P;
import org.smartut.symbolic.vm.math.MAX;
import org.smartut.symbolic.vm.math.MIN;
import org.smartut.symbolic.vm.math.NextAfter;
import org.smartut.symbolic.vm.math.NextUp;
import org.smartut.symbolic.vm.math.POW;
import org.smartut.symbolic.vm.math.RINT;
import org.smartut.symbolic.vm.math.Round;
import org.smartut.symbolic.vm.math.SCALB;
import org.smartut.symbolic.vm.math.SIGNUM;
import org.smartut.symbolic.vm.math.SIN;
import org.smartut.symbolic.vm.math.SINH;
import org.smartut.symbolic.vm.math.SQRT;
import org.smartut.symbolic.vm.math.TAN;
import org.smartut.symbolic.vm.math.TANH;
import org.smartut.symbolic.vm.math.ToDegrees;
import org.smartut.symbolic.vm.math.ToRadians;
import org.smartut.symbolic.vm.math.ULP;
import org.smartut.symbolic.vm.regex.Matcher_Matches;
import org.smartut.symbolic.vm.regex.Pattern_Matcher;
import org.smartut.symbolic.vm.regex.Pattern_Matches;
import org.smartut.symbolic.vm.string.CharAt;
import org.smartut.symbolic.vm.string.CompareTo;
import org.smartut.symbolic.vm.string.CompareToIgnoreCase;
import org.smartut.symbolic.vm.string.Concat;
import org.smartut.symbolic.vm.string.Contains;
import org.smartut.symbolic.vm.string.EndsWith;
import org.smartut.symbolic.vm.string.Equals;
import org.smartut.symbolic.vm.string.EqualsIgnoreCase;
import org.smartut.symbolic.vm.string.IndexOf;
import org.smartut.symbolic.vm.string.LastIndexOf;
import org.smartut.symbolic.vm.string.Length;
import org.smartut.symbolic.vm.string.Matches;
import org.smartut.symbolic.vm.string.RegionMatches;
import org.smartut.symbolic.vm.string.RegionMatches5;
import org.smartut.symbolic.vm.string.Replace;
import org.smartut.symbolic.vm.string.ReplaceAll;
import org.smartut.symbolic.vm.string.ReplaceFirst;
import org.smartut.symbolic.vm.string.StartsWith;
import org.smartut.symbolic.vm.string.Substring;
import org.smartut.symbolic.vm.string.ToLowerCase;
import org.smartut.symbolic.vm.string.ToString;
import org.smartut.symbolic.vm.string.ToUpperCase;
import org.smartut.symbolic.vm.string.Trim;
import org.smartut.symbolic.vm.string.ValueOf;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_B;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_C;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_D;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_F;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_I;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_L;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Append.StringBufferAppend_STR;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_Init.StringBufferInit_S;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_SetLength;
import org.smartut.symbolic.vm.string.buffer.StringBuffer_ToString;
import org.smartut.symbolic.vm.string.builder.StringBuilder_Append;
import org.smartut.symbolic.vm.string.builder.StringBuilder_Init;
import org.smartut.symbolic.vm.string.builder.StringBuilder_ToString;
import org.smartut.symbolic.vm.string.reader.Reader_Read;
import org.smartut.symbolic.vm.string.reader.StringReader_Init;
import org.smartut.symbolic.vm.string.reader.StringReader_Read;
import org.smartut.symbolic.vm.string.tokenizer.HasMoreTokens;
import org.smartut.symbolic.vm.string.tokenizer.NextToken;
import org.smartut.symbolic.vm.string.tokenizer.StringTokenizer_Init;
import org.smartut.symbolic.vm.wrappers.B_ByteValue;
import org.smartut.symbolic.vm.wrappers.B_Init;
import org.smartut.symbolic.vm.wrappers.B_ValueOf;
import org.smartut.symbolic.vm.wrappers.C_CharValue;
import org.smartut.symbolic.vm.wrappers.C_Init;
import org.smartut.symbolic.vm.wrappers.C_ValueOf;
import org.smartut.symbolic.vm.wrappers.Character_getNumericValue;
import org.smartut.symbolic.vm.wrappers.Character_isDigit;
import org.smartut.symbolic.vm.wrappers.Character_isLetter;
import org.smartut.symbolic.vm.wrappers.D_DoubleValue;
import org.smartut.symbolic.vm.wrappers.D_Init;
import org.smartut.symbolic.vm.wrappers.D_ValueOf;
import org.smartut.symbolic.vm.wrappers.F_FloatValue;
import org.smartut.symbolic.vm.wrappers.F_Init;
import org.smartut.symbolic.vm.wrappers.F_ValueOf;
import org.smartut.symbolic.vm.wrappers.I_Init;
import org.smartut.symbolic.vm.wrappers.I_IntValue;
import org.smartut.symbolic.vm.wrappers.I_ParseInt;
import org.smartut.symbolic.vm.wrappers.I_ValueOf;
import org.smartut.symbolic.vm.wrappers.J_Init;
import org.smartut.symbolic.vm.wrappers.J_LongValue;
import org.smartut.symbolic.vm.wrappers.J_ValueOf;
import org.smartut.symbolic.vm.wrappers.S_Init;
import org.smartut.symbolic.vm.wrappers.S_ShortValue;
import org.smartut.symbolic.vm.wrappers.S_ValueOf;
import org.smartut.symbolic.vm.wrappers.Z_BooleanValue;
import org.smartut.symbolic.vm.wrappers.Z_Init;
import org.smartut.symbolic.vm.wrappers.Z_ValueOf;
import org.objectweb.asm.Type;

import org.smartut.dse.AbstractVM;

/**
 * 
 * @author galeotti
 * 
 */
public final class SymbolicFunctionVM extends AbstractVM {

	private static class FunctionKey {
		public FunctionKey(String owner, String name, String desc) {
			super();
			this.owner = owner;
			this.name = name;
			this.desc = desc;
		}

		public String owner;
		public String name;
		public String desc;

		@Override
		public int hashCode() {
			return owner.hashCode() + name.hashCode() + desc.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !o.getClass().equals(FunctionKey.class)) {
				return false;
			} else {
				FunctionKey that = (FunctionKey) o;
				return this.owner.equals(that.owner)
						&& this.name.equals(that.name)
						&& this.desc.equals(that.desc);
			}
		}
	}

	private final SymbolicEnvironment env;
	private final PathConditionCollector pc;

	public SymbolicFunctionVM(SymbolicEnvironment env, PathConditionCollector pc) {
		this.env = env;
		this.pc = pc;
		fillFunctionsTable();
	}

	private void fillFunctionsTable() {

		// java.lang.StringBuffer
		addFunctionToTable(new StringBufferInit_S(env));
		addFunctionToTable(new StringBuffer_ToString(env));
		addFunctionToTable(new StringBufferAppend_B(env));
		addFunctionToTable(new StringBufferAppend_C(env));
		addFunctionToTable(new StringBufferAppend_I(env));
		addFunctionToTable(new StringBufferAppend_L(env));
		addFunctionToTable(new StringBufferAppend_F(env));
		addFunctionToTable(new StringBufferAppend_D(env));
		addFunctionToTable(new StringBufferAppend_STR(env));
		addFunctionToTable(new StringBuffer_SetLength(env));

		// java.lang.Integer
		addFunctionToTable(new I_Init(env));
		addFunctionToTable(new I_ValueOf(env));
		addFunctionToTable(new I_IntValue(env));
		addFunctionToTable(new I_ParseInt(env));

		// java.lang.Long
		addFunctionToTable(new J_Init(env));
		addFunctionToTable(new J_ValueOf(env));
		addFunctionToTable(new J_LongValue(env));
		//
		// java.lang.Float
		addFunctionToTable(new F_Init(env));
		addFunctionToTable(new F_ValueOf(env));
		addFunctionToTable(new F_FloatValue(env));
		//
		// java.lang.Double
		addFunctionToTable(new D_Init(env));
		addFunctionToTable(new D_ValueOf(env));
		addFunctionToTable(new D_DoubleValue(env));
		//
		// java.lang.Short
		addFunctionToTable(new S_Init(env));
		addFunctionToTable(new S_ValueOf(env));
		addFunctionToTable(new S_ShortValue(env));
		//
		// java.lang.Byte
		addFunctionToTable(new B_Init(env));
		addFunctionToTable(new B_ValueOf(env));
		addFunctionToTable(new B_ByteValue(env));
		//
		// java.lang.Character
		addFunctionToTable(new C_Init(env));
		addFunctionToTable(new C_ValueOf(env));
		addFunctionToTable(new C_CharValue(env));
		addFunctionToTable(new Character_getNumericValue(env));
		addFunctionToTable(new Character_isDigit(env));
		addFunctionToTable(new Character_isLetter(env));
		//
		// java.lang.Boolean
		addFunctionToTable(new Z_Init(env));
		addFunctionToTable(new Z_ValueOf(env));
		addFunctionToTable(new Z_BooleanValue(env));
		//
		// java.lang.Math
		addFunctionToTable(new ABS.ABS_I(env));
		addFunctionToTable(new ABS.ABS_L(env));
		addFunctionToTable(new ABS.ABS_F(env));
		addFunctionToTable(new ABS.ABS_D(env));
		addFunctionToTable(new ACOS(env));
		addFunctionToTable(new ASIN(env));
		addFunctionToTable(new ATAN(env));
		addFunctionToTable(new ATAN2(env));
		addFunctionToTable(new CBRT(env));
		addFunctionToTable(new CEIL(env));
		addFunctionToTable(new CopySign.CopySign_F(env));
		addFunctionToTable(new CopySign.CopySign_D(env));
		addFunctionToTable(new COS(env));
		addFunctionToTable(new COSH(env));
		addFunctionToTable(new EXP(env));
		addFunctionToTable(new EXPM1(env));
		addFunctionToTable(new FLOOR(env));
		addFunctionToTable(new GetExponent.GetExponent_F(env));
		addFunctionToTable(new GetExponent.GetExponent_D(env));
		addFunctionToTable(new HYPOT(env));
		addFunctionToTable(new IEEEremainder(env));
		addFunctionToTable(new LOG(env));
		addFunctionToTable(new LOG10(env));
		addFunctionToTable(new LOG1P(env));
		addFunctionToTable(new MIN.MIN_I(env));
		addFunctionToTable(new MIN.MIN_L(env));
		addFunctionToTable(new MIN.MIN_F(env));
		addFunctionToTable(new MIN.MIN_D(env));
		addFunctionToTable(new MAX.MAX_I(env));
		addFunctionToTable(new MAX.MAX_L(env));
		addFunctionToTable(new MAX.MAX_F(env));
		addFunctionToTable(new MAX.MAX_D(env));
		addFunctionToTable(new NextAfter.NextAfter_F(env));
		addFunctionToTable(new NextAfter.NextAfter_D(env));
		addFunctionToTable(new NextUp.NextUp_F(env));
		addFunctionToTable(new NextUp.NextUp_D(env));
		addFunctionToTable(new POW(env));
		addFunctionToTable(new RINT(env));
		addFunctionToTable(new Round.Round_F(env));
		addFunctionToTable(new Round.Round_D(env));
		addFunctionToTable(new SCALB.SCALB_F(env));
		addFunctionToTable(new SCALB.SCALB_D(env));
		addFunctionToTable(new SIGNUM.SIGNUM_F(env));
		addFunctionToTable(new SIGNUM.SIGNUM_D(env));
		addFunctionToTable(new SIN(env));
		addFunctionToTable(new SINH(env));
		addFunctionToTable(new SQRT(env));
		addFunctionToTable(new TAN(env));
		addFunctionToTable(new TANH(env));
		addFunctionToTable(new ToDegrees(env));
		addFunctionToTable(new ToRadians(env));
		addFunctionToTable(new ULP.ULP_F(env));
		addFunctionToTable(new ULP.ULP_D(env));

		// java.lang.String
		addFunctionToTable(new CharAt(env));
		addFunctionToTable(new CompareTo(env));
		addFunctionToTable(new CompareToIgnoreCase(env));
		addFunctionToTable(new Concat(env));
		addFunctionToTable(new Contains(env));
		addFunctionToTable(new EndsWith(env));
		addFunctionToTable(new Equals(env));
		addFunctionToTable(new EqualsIgnoreCase(env));
		addFunctionToTable(new IndexOf.IndexOf_C(env));
		addFunctionToTable(new IndexOf.IndexOf_S(env));
		addFunctionToTable(new IndexOf.IndexOf_CI(env));
		addFunctionToTable(new IndexOf.IndexOf_SI(env));
		addFunctionToTable(new LastIndexOf.LastIndexOf_C(env));
		addFunctionToTable(new LastIndexOf.LastIndexOf_S(env));
		addFunctionToTable(new LastIndexOf.LastIndexOf_CI(env));
		addFunctionToTable(new LastIndexOf.LastIndexOf_SI(env));
		addFunctionToTable(new Length(env));
		addFunctionToTable(new Matches(env));
		addFunctionToTable(new RegionMatches(env));
		addFunctionToTable(new RegionMatches5(env));
		addFunctionToTable(new Replace.Replace_C(env));
		addFunctionToTable(new Replace.Replace_CS(env));
		addFunctionToTable(new ReplaceAll(env));
		addFunctionToTable(new ReplaceFirst(env));
		addFunctionToTable(new StartsWith.StartsWith_S(env));
		addFunctionToTable(new StartsWith.StartsWith_SI(env));
		addFunctionToTable(new Substring.Substring_I(env));
		addFunctionToTable(new Substring.Substring_II(env));
		addFunctionToTable(new ToLowerCase(env));
		addFunctionToTable(new ToString(env));
		addFunctionToTable(new ToUpperCase(env));
		addFunctionToTable(new Trim(env));
		addFunctionToTable(new ValueOf.ValueOf_O(env));
		addFunctionToTable(new ValueOf.ValueOf_I(env));
		addFunctionToTable(new ValueOf.ValueOf_J(env));
		addFunctionToTable(new ValueOf.ValueOf_C(env));
		addFunctionToTable(new ValueOf.ValueOf_B(env));

		// java.lang.StringBuilder
		addFunctionToTable(new StringBuilder_Init(env));
		addFunctionToTable(new StringBuilder_Append.Append_B(env));
		addFunctionToTable(new StringBuilder_Append.Append_C(env));
		addFunctionToTable(new StringBuilder_Append.Append_D(env));
		addFunctionToTable(new StringBuilder_Append.Append_F(env));
		addFunctionToTable(new StringBuilder_Append.Append_I(env));
		addFunctionToTable(new StringBuilder_Append.Append_L(env));
		addFunctionToTable(new StringBuilder_Append.Append_O(env));
		addFunctionToTable(new StringBuilder_Append.Append_S(env));
		addFunctionToTable(new StringBuilder_ToString(env));

		// java.util.StringTokenizer
		addFunctionToTable(new StringTokenizer_Init(env));
		addFunctionToTable(new HasMoreTokens(env));
		addFunctionToTable(new NextToken(env));

		// java.io.StringReader
		addFunctionToTable(new StringReader_Init(env));
		addFunctionToTable(new StringReader_Read(env));

		// java.io.Reader
		addFunctionToTable(new Reader_Read(env));

		// java.util.regex.Pattern
		addFunctionToTable(new Pattern_Matches(env));
		addFunctionToTable(new Pattern_Matcher(env));

		// java.util.regex.Matcher
		addFunctionToTable(new Matcher_Matches(env));

		// org.apache.oro.text.regex
		addFunctionToTable(new Perl5Matcher_Matches(env));

		// java.math.BigInteger
		addFunctionToTable(new BigInteger_Ctor(env));
		addFunctionToTable(new BigInteger_IntValue(env));
		addFunctionToTable(new BigInteger_DivideAndRemainder(env));

	}

	private void addFunctionToTable(SymbolicFunction f) {
		FunctionKey k = new FunctionKey(f.getOwner(), f.getName(), f.getDesc());
		functionsTable.put(k, f);
	}

	@Override
	public void INVOKESTATIC(String owner, String name, String desc) {
		functionUnderExecution = getFunction(owner, name, desc);
		if (functionUnderExecution != null) {
			if (Type.getArgumentTypes(desc).length == 0) {
				callBeforeExecution(functionUnderExecution);
			}
		}
	}

	@Override
	public void INVOKEVIRTUAL(Object conc_receiver, String owner, String name,
			String desc) {
		functionUnderExecution = getFunction(owner, name, desc);
		if (functionUnderExecution != null) {
			ReferenceExpression symb_receiver = getReceiverFromStack();
			functionUnderExecution.setReceiver(conc_receiver, symb_receiver);
			if (Type.getArgumentTypes(desc).length == 0) {
				callBeforeExecution(functionUnderExecution);
			}
		}
	}

	private ReferenceExpression getReceiverFromStack() {
		String desc = this.functionUnderExecution.getDesc();
		Type[] argTypes = Type.getArgumentTypes(desc);
		Iterator<Operand> it = this.env.topFrame().operandStack.iterator();
		for (int i = 0; i < argTypes.length; i++) {
			it.next(); // move cursor until reference
		}
		Operand op = it.next();
		return ((ReferenceOperand) op).getReference();
	}

	private Operand getOperandFromStack(int nr) {
		String desc = functionUnderExecution.getDesc();
		Type[] argTypes = Type.getArgumentTypes(desc);
		int moves = argTypes.length - 1 - nr;

		Iterator<Operand> it = this.env.topFrame().operandStack.iterator();
		for (int i = 0; i < moves; i++) {
			it.next(); // move cursor until reference
		}
		Operand op = it.next();
		return op;
	}

	private IntegerValue getIntegerExprFromStack(int nr) {
		IntegerOperand op = (IntegerOperand) getOperandFromStack(nr);
		return op.getIntegerExpression();
	}

	private RealValue getRealExprFromStack(int moves) {
		RealOperand op = (RealOperand) getOperandFromStack(moves);
		return op.getRealExpression();
	}

	private ReferenceExpression getReferenceFromStack(int moves) {
		ReferenceOperand op = (ReferenceOperand) getOperandFromStack(moves);
		return op.getReference();
	}

	private final Map<FunctionKey, SymbolicFunction> functionsTable = new HashMap<>();

	private SymbolicFunction getFunction(String owner, String name, String desc) {
		FunctionKey k = new FunctionKey(owner, name, desc);
		SymbolicFunction f = functionsTable.get(k);
		return f;
	}

	@Override
	public void CALL_RESULT(int conc_ret_val, String owner, String name,
			String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			IntegerValue symb_ret_val = this.env.topFrame().operandStack
					.peekBv32();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			IntegerValue new_symb_ret_val = (IntegerValue) functionUnderExecution
					.executeFunction();
			this.replaceTopBv32(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	@Override
	public void CALL_RESULT(Object conc_ret_val, String owner, String name,
			String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			ReferenceExpression symb_ret_val = this.env.topFrame().operandStack.peekRef();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			ReferenceExpression new_symb_ret_val = (ReferenceExpression) functionUnderExecution
					.executeFunction();
			this.replaceTopRef(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	private void replaceTopRef(ReferenceExpression ref) {
		env.topFrame().operandStack.popRef();
		env.topFrame().operandStack.pushRef(ref);
	}

	@Override
	public void CALL_RESULT(String owner, String name, String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {
				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			functionUnderExecution.executeFunction();
		}
		functionUnderExecution = null;
	}

	@Override
	public void CALL_RESULT(boolean conc_ret_val, String owner, String name,
			String desc) {
		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {

			IntegerValue symb_ret_val = this.env.topFrame().operandStack
					.peekBv32();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			IntegerValue new_symb_ret_val = (IntegerValue) functionUnderExecution
					.executeFunction();
			this.replaceTopBv32(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	@Override
	public void CALL_RESULT(long conc_ret_val, String owner, String name,
			String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			IntegerValue symb_ret_val = this.env.topFrame().operandStack
					.peekBv64();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			IntegerValue new_symb_ret_val = (IntegerValue) functionUnderExecution
					.executeFunction();
			this.replaceTopBv64(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	@Override
	public void CALL_RESULT(double conc_ret_val, String owner, String name,
			String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			RealValue symb_ret_val = this.env.topFrame().operandStack
					.peekFp64();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			RealValue new_symb_ret_val = (RealValue) functionUnderExecution
					.executeFunction();
			this.replaceTopFp64(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	@Override
	public void CALL_RESULT(float conc_ret_val, String owner, String name,
			String desc) {

		if (functionUnderExecution != null) {
			if (!functionUnderExecution.getOwner().equals(owner)
					|| !functionUnderExecution.getName().equals(name)
					|| !functionUnderExecution.getDesc().equals(desc)) {

				functionUnderExecution = null;
			}
		}

		if (functionUnderExecution != null) {
			RealValue symb_ret_val = this.env.topFrame().operandStack
					.peekFp32();
			functionUnderExecution.setReturnValue(conc_ret_val, symb_ret_val);
			RealValue new_symb_ret_val = (RealValue) functionUnderExecution
					.executeFunction();
			this.replaceTopFp32(new_symb_ret_val);
		}
		functionUnderExecution = null;
	}

	@Override
	public void INVOKESPECIAL(String owner, String name, String desc) {
		functionUnderExecution = getFunction(owner, name, desc);
		if (functionUnderExecution != null) {
			ReferenceExpression symb_receiver = getReceiverFromStack();
			functionUnderExecution.setReceiver(
					null /* receiver not yet ready */, symb_receiver);
			if (Type.getArgumentTypes(desc).length == 0) {
				callBeforeExecution(functionUnderExecution);
			}
		}
	}

	private void callBeforeExecution(SymbolicFunction myFunctionUnderExecution) {
		IntegerConstraint constraint = myFunctionUnderExecution
				.beforeExecuteFunction();
		if (constraint != null) {
			pc.appendSupportingConstraint(constraint);
		}
	}

	@Override
	public void INVOKESPECIAL(Object conc_receiver, String owner, String name,
			String desc) {
		functionUnderExecution = getFunction(owner, name, desc);
		if (functionUnderExecution != null) {
			ReferenceExpression symb_receiver = getReceiverFromStack();
			functionUnderExecution.setReceiver(conc_receiver, symb_receiver);
			if (Type.getArgumentTypes(desc).length == 0) {
				callBeforeExecution(functionUnderExecution);
			}
		}

	}

	private SymbolicFunction functionUnderExecution;

	@Override
	public void INVOKEINTERFACE(Object conc_receiver, String owner,
			String name, String desc) {
		functionUnderExecution = getFunction(owner, name, desc);
		if (functionUnderExecution != null) {
			ReferenceExpression symb_receiver = getReceiverFromStack();
			functionUnderExecution.setReceiver(conc_receiver, symb_receiver);
			if (Type.getArgumentTypes(functionUnderExecution.getDesc()).length == 0) {
				callBeforeExecution(functionUnderExecution);
			}
		}

	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, int conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex,
			boolean conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);
		}
	}

	private void beforeExecuteFunction(int nr) {
//		String desc = functionUnderExecution.getDesc();
		if (nr ==0) {
			callBeforeExecution(functionUnderExecution);
		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, short conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, byte conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, char conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, long conc_arg) {
		if (functionUnderExecution != null) {
			IntegerValue symb_arg = getIntegerExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex, float conc_arg) {
		if (functionUnderExecution != null) {
			RealValue symb_arg = getRealExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex,
			double conc_arg) {
		if (functionUnderExecution != null) {
			RealValue symb_arg = getRealExprFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	@Override
	public void CALLER_STACK_PARAM(int nr, int calleeLocalsIndex,
			Object conc_arg) {
		if (functionUnderExecution != null) {
			ReferenceExpression symb_arg = getReferenceFromStack(nr);
			functionUnderExecution.setParam(nr, conc_arg, symb_arg);
			beforeExecuteFunction(nr);

		}
	}

	private void replaceTopBv32(IntegerValue expr) {
		env.topFrame().operandStack.popBv32();
		env.topFrame().operandStack.pushBv32(expr);
	}

	private void replaceTopBv64(IntegerValue expr) {
		env.topFrame().operandStack.popBv64();
		env.topFrame().operandStack.pushBv64(expr);
	}

	private void replaceTopFp32(RealValue expr) {
		this.env.topFrame().operandStack.popFp32();
		this.env.topFrame().operandStack.pushFp32(expr);
	}

	private void replaceTopFp64(RealValue expr) {
		env.topFrame().operandStack.popFp64();
		env.topFrame().operandStack.pushFp64(expr);
	}

}
