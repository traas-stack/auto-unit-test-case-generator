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
package org.smartut.testcase.statements;


import com.examples.with.different.packagename.fm.IssueWithNumber;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.smartut.Properties;
import org.smartut.classpath.ClassPathHandler;
import org.smartut.instrumentation.InstrumentingClassLoader;
import org.smartut.instrumentation.NonInstrumentingClassLoader;
import org.smartut.runtime.RuntimeSettings;
import org.smartut.runtime.instrumentation.SmartUtClassLoader;
import org.smartut.runtime.instrumentation.RuntimeInstrumentation;
import org.smartut.testcase.DefaultTestCase;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.numeric.BooleanPrimitiveStatement;
import org.smartut.testcase.statements.numeric.IntPrimitiveStatement;
import org.smartut.testcase.variable.ArrayIndex;
import org.smartut.testcase.variable.ArrayReference;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.variable.VariableReferenceImpl;
import org.smartut.utils.generic.GenericClass;
import org.smartut.utils.generic.GenericMethod;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Andrea Arcuri on 06/08/15.
 */
public class FunctionalMockStatementTest {

    private static final int DEFAULT_LIMIT = Properties.FUNCTIONAL_MOCKING_INPUT_LIMIT;

    @After
    public void tearDown(){
        Properties.FUNCTIONAL_MOCKING_INPUT_LIMIT = DEFAULT_LIMIT;
    }

    public interface Foo{
        boolean getBoolean();
        int getInt();
        double getDouble();
        String getString();
        long getLong();
        Object getObject();
        String[] getStringArray(int[] input);
    }

    public static int base(Foo foo){
        return foo.getInt();
    }

    public static void all_once(Foo foo){
        foo.getBoolean();
        foo.getInt();
        foo.getDouble();
        foo.getString();
        foo.getLong();
        foo.getObject();
        foo.getStringArray(null);
    }

    public static void all_twice(Foo foo){
        all_once(foo);
        all_once(foo);
    }

    public static String getFirstInArray(Foo foo){
        int[] anArray = new int[]{123};
        String[] res = foo.getStringArray(anArray);
        if(res==null){
            return null;
        }
        return res[0];
    }

    public static void limit(Foo foo, int x){
        for(int i=0; i<x ; i++){
            foo.getBoolean();
        }
    }

    private Scope execute(TestCase tc) throws Exception{
        Scope scope = new Scope();
        for(Statement st : tc){
            st.execute(scope,System.out);
        }
        return scope;
    }

    static class PackageLevel{
        PackageLevel(){}
    }

    public static class AClassWithPLMethod{

        String foo(){
            return "Value returned by package-level access method";
        }
    }

    public static class OverrideToString{
        @Override
        public String toString(){
            return "foo";
        }
    }

    public static abstract class OverrideToStringAbstract  implements java.io.Serializable {
        @Override
        public String toString(){
            return "foo";
        }

        public abstract double foo();

        public int bar(){return 1;}

        private static final long serialVersionUID = -8742448824652078965L;
    }

    //----------------------------------------------------------------------------------




    @Ignore
    @Test
    public void testAClassWithPLMethod() {

        //FIXME once we support it
        assertFalse(FunctionalMockStatement.canBeFunctionalMocked(AClassWithPLMethod.class));
    }

    @Test
    public void testConfirmToString(){
        String res = new OverrideToString().toString();
        String diff = res + " a different string";

        OverrideToString obj = mock(OverrideToString.class);
        when(obj.toString()).thenReturn(diff);

        assertEquals(diff, obj.toString());
    }

    @Test
    public void testConfirmToStringAbstract(){

        String diff = " a different string";

        OverrideToStringAbstract obj = mock(OverrideToStringAbstract.class);
        when(obj.toString()).thenReturn(diff);

        assertEquals(diff, obj.toString());
    }

    @Test
    public void testConfirmNumber(){
        String foo = "foo";
        Number number = mock(Number.class);
        when(number.toString()).thenReturn(foo);

        assertEquals(foo, number.toString());
    }


    @Test
    public void testConfirmNumberExternalNoMockJVMNonDeterminism() throws Exception{
        RuntimeSettings.mockJVMNonDeterminism = false;
        testConfirmNumberExternal();
    }

    @Test
    public void testConfirmNumberExternalWithMockJVMNonDeterminism() throws Exception{
        RuntimeSettings.mockJVMNonDeterminism = true;
        testConfirmNumberExternal();
    }


    private void testConfirmNumberExternal() throws Exception{
        assertEquals(IssueWithNumber.RESULT, IssueWithNumber.getResult());

        RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(true);


        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
        SmartUtClassLoader loader = new SmartUtClassLoader();
        loader.skipInstrumentation(IssueWithNumber.class.getName());
        org.smartut.runtime.Runtime.getInstance().resetRuntime();
        Class<?> klass = loader.loadClass(IssueWithNumber.class.getName());
        Method m = klass.getDeclaredMethod("getResult");
        String res = (String) m.invoke(null);

        assertEquals(IssueWithNumber.RESULT, res);
    }



    @Test
    public void testConfirmPackageLevel() throws Exception{

        Method m = AClassWithPLMethod.class.getDeclaredMethod("foo");
        assertFalse(Modifier.isPrivate(m.getModifiers()));
        assertFalse(Modifier.isPublic(m.getModifiers()));
        assertFalse(Modifier.isProtected(m.getModifiers()));
    }

    @Test
    public void testConfirmMockitoBehaviorOnPackageLevelAccess() throws Exception {

        //direct calls

        AClassWithPLMethod original = new AClassWithPLMethod();
        assertNotNull(original.foo());

        AClassWithPLMethod mocked = mock(AClassWithPLMethod.class);
        assertNull(mocked.foo());


        //reflection
        Method m = AClassWithPLMethod.class.getDeclaredMethod("foo");
        m.setAccessible(true);

        assertNotNull(m.invoke(original));
        assertNull(m.invoke(mocked));
    }


    @Test
    public void testConfirmCast(){

        //note: TypeUtils can give different results because it takes autoboxing into account

        assertTrue(TypeUtils.isAssignable(Integer.class, Integer.TYPE));
        assertTrue(TypeUtils.isAssignable(Integer.TYPE, Integer.class));
        assertFalse(Integer.TYPE.isAssignableFrom(Integer.class));
        assertFalse(Integer.class.isAssignableFrom(Integer.TYPE));


        assertFalse(Integer.TYPE.isAssignableFrom(Character.TYPE));
        assertFalse(TypeUtils.isAssignable(Integer.TYPE, Character.TYPE));

        assertFalse(Character.TYPE.isAssignableFrom(Integer.TYPE));
        assertTrue(TypeUtils.isAssignable(Character.TYPE, Integer.TYPE)); //DIFFERENT

        assertFalse(Character.class.isAssignableFrom(Integer.TYPE));
        assertTrue(TypeUtils.isAssignable(Character.class, Integer.TYPE)); //DIFFERENT

        assertFalse(Character.class.isAssignableFrom(Integer.class));
        assertFalse(TypeUtils.isAssignable(Character.class, Integer.class));

        assertTrue(Integer.TYPE.isPrimitive());
        assertFalse(Integer.class.isPrimitive());


        char c = 'c'; //99
        int i = c;

        assertEquals(99, i);

        Object aInt = i;
        Object aInteger = 7;

        assertEquals(aInt.getClass(), Integer.class);
        assertEquals(aInt.getClass(), aInteger.getClass());

        Object aChar = c;
        assertEquals(aChar.getClass(), Character.class);

        //just recall the two diverge
        assertTrue(TypeUtils.isAssignable(aChar.getClass(), Integer.TYPE));
        assertFalse(Integer.TYPE.isAssignableFrom(aChar.getClass()));

        Object casted = null;
        try {
            casted = Integer.TYPE.cast(aChar);
            fail();
        } catch (Exception e){
            //expected: cannot do direct cast from "Character" to "int"
        }

        try {
            casted = Integer.TYPE.cast(aChar);
            fail();
        } catch (Exception e){
            //expected: "cast" takes an Object as input, so it does autoboxing :(
        }

        casted = (int) (Character) aChar;

        assertEquals(casted.getClass(), Integer.class);
    }

    @Test
    public void testAvoidMockingEnvironment(){
        final boolean defaultValue = RuntimeSettings.useVFS;
        RuntimeSettings.useVFS = true;

        try {
            Assert.assertFalse(FunctionalMockStatement.canBeFunctionalMocked(File.class));
        } catch(Throwable t){
            RuntimeSettings.useVFS = defaultValue;
        }
    }


    @Test
    public void testPackageLevel_local()  throws Exception{
        TestCase tc = new DefaultTestCase();

        VariableReference ref = new VariableReferenceImpl(tc, PackageLevel.class);

        try {
            FunctionalMockStatement mockStmt = new FunctionalMockStatement(tc, ref, new GenericClass(PackageLevel.class));
            fail();
        } catch (java.lang.IllegalArgumentException e){
            //expected
        }

        //tc.addStatement(mockStmt);
        //execute(tc);
    }


    @Test
    public void testPackageLevel_differentPackage()  throws Exception{
        TestCase tc = new DefaultTestCase();

        Class<?> example = Class.forName("com.examples.with.different.packagename.fm.ExamplePackageLevel");

        VariableReference ref = new VariableReferenceImpl(tc, example);

        try {
            FunctionalMockStatement mockStmt = new FunctionalMockStatement(tc, ref, new GenericClass(example));
            fail();
        } catch (java.lang.IllegalArgumentException e){
            //expected
        }

        //tc.addStatement(mockStmt);
        //execute(tc);
    }

    @Test
    public void testPackageLevel_differentPackage_instrumentation_package()  throws Exception{
        TestCase tc = new DefaultTestCase();

        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
        InstrumentingClassLoader loader = new InstrumentingClassLoader();
        Class<?> example = loader.loadClass("com.examples.with.different.packagename.fm.ExamplePackageLevel");

        VariableReference ref = new VariableReferenceImpl(tc, example);

        try {
            FunctionalMockStatement mockStmt = new FunctionalMockStatement(tc, ref, new GenericClass(example));
            fail();
        } catch (java.lang.IllegalArgumentException e){
            //expected
        }

        //tc.addStatement(mockStmt);
        //execute(tc);
    }

    @Test
    public void testPackageLevel_differentPackage_nonInstrumentation_package()  throws Exception{
        TestCase tc = new DefaultTestCase();

        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
        NonInstrumentingClassLoader loader = new NonInstrumentingClassLoader();
        Class<?> example = loader.loadClass("com.examples.with.different.packagename.fm.ExamplePackageLevel");

        VariableReference ref = new VariableReferenceImpl(tc, example);

        try {
            FunctionalMockStatement mockStmt = new FunctionalMockStatement(tc, ref, new GenericClass(example));
            fail();
        } catch (java.lang.IllegalArgumentException e){
            //expected
        }

        //tc.addStatement(mockStmt);
        //execute(tc);
    }

    /*
     * This test fails when Mockito is instrumented (it would pass if org.mockito. is excluded from instrumentation.
     * However, in the packaged version, Mockito is shaded and thus isn't actually instrumented. I don't have a good
     * solution to fix this test, hence I've marked it as ignored. (Gordon, 9.2.2018)
     */
    @Test
    public void testPackageLevel_differentPackage_instrumentation_public()  throws Exception{
        TestCase tc = new DefaultTestCase();

        RuntimeInstrumentation.setAvoidInstrumentingShadedClasses(true);

        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsSmartUt();
        InstrumentingClassLoader loader = new InstrumentingClassLoader();
        Class<?> example = loader.loadClass("com.examples.with.different.packagename.fm.ExamplePublicLevel");

        VariableReference ref = new VariableReferenceImpl(tc, example);
        FunctionalMockStatement mockStmt = new FunctionalMockStatement(tc, ref, new GenericClass(example));

        tc.addStatement(mockStmt);
        execute(tc);
    }

}