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
package org.smartut.testcase.fm;

import com.googlecode.gentyref.GenericTypeReflector;
import org.mockito.internal.invocation.InterceptedInvocation;
import org.smartut.Properties;
import org.smartut.seeding.CastClassManager;
import org.smartut.utils.LoggingUtils;
import org.smartut.utils.generic.GenericClass;
import org.mockito.invocation.DescribedInvocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * During the test generation, we need to know which methods have been called,
 * and how often they were called.
 * This is however not needed in the final generated JUnit tests
 *
 * Created by Andrea Arcuri on 27/07/15.
 */
public class EvoInvocationListener implements InvocationListener, Serializable {

	private static final long serialVersionUID = 8351121388007697168L;

	private final Map<String, MethodDescriptor> map = new LinkedHashMap<>();

    /**
     * By default, we should not log events, otherwise we would end up
     * logging also cases like "when(...)" which are set before a mock is used
     */
    private volatile boolean active = false;

    private final GenericClass retvalType;

    /**
     * record same method (e.g. T method(T) )has been invoked times
     */
    private AtomicInteger count = new AtomicInteger(0);

    public EvoInvocationListener(Type retvalType){
        this.retvalType = new GenericClass(retvalType);
    }


    public EvoInvocationListener(GenericClass retvalType){
        this.retvalType = retvalType;
    }

    public void activate(){
        active = true;
    }


    public void changeClassLoader(ClassLoader loader) {
        for(MethodDescriptor descriptor : map.values()){
            if(descriptor != null){
                descriptor.changeClassLoader(loader);
            }
        }
    }

    /**
     *
     * @return a sorted list
     */
    public List<MethodDescriptor> getCopyOfMethodDescriptors(){
        return map.values().stream().sorted().collect(Collectors.toList());
    }

    protected boolean onlyMockAbstractMethods() {
        return false;
    }

    @Override
    public void reportInvocation(MethodInvocationReport methodInvocationReport) {

        if(! active){
            return;
        }

        DescribedInvocation di = methodInvocationReport.getInvocation();
        MethodDescriptor md = null;

        /**
         * record actual return type
         * used for TypeVariable & cast
         * e.g.
         * 1. T get(Class<T> clazz);
         * 2. SpecialClass clazz = (SpecialClass) method.invoke(params);
          */
        Type actualReturnType;
        if(di instanceof InvocationOnMock){
            InvocationOnMock impl = (InvocationOnMock) di;
            Method method = impl.getMethod();

            // first analyze special return type
            actualReturnType = typeAnalyze(di);
            if(null != actualReturnType) {
                md = new MethodDescriptor(method, retvalType, actualReturnType, count.incrementAndGet());
            } else {
                md = new MethodDescriptor(method, retvalType);
            }
        } else {
            //hopefully it should never happen
            md = getMethodDescriptor_old(di);
        }

        if(md.getMethodName().equals("finalize")){
            //ignore it, otherwise if we mock it, we ll end up in a lot of side effects... :(
            return;
        }

        if(onlyMockAbstractMethods() && !md.getGenericMethod().isAbstract()) {
            return;
        }

        synchronized (map){
            MethodDescriptor current = map.get(md.getID());
            if(current == null){
                current = md;
            }
            current.increaseCounter();
            map.put(md.getID(),current);
        }
    }

    /**
     * Analyze actual type of method return value
     * @param di  DescribedInvocation
     * @return    actual return type, return null if not belong to special cases
     */
    private Type typeAnalyze(DescribedInvocation di) {
        InvocationOnMock impl = (InvocationOnMock) di;

        // 1. Handle TypeVariable
        Type typeParamActualVal = typeParamReturnAnalyze(impl);
        if( null != typeParamActualVal) {
            return typeParamActualVal;
        }

        // 2. Handle cast case
        return resolveReturnCastType(di);
    }

    /**
     * get actual type when method return value has been cast explicitly
     * @param di DescribedInvocation
     * @return   cast type, return null if not belong to special cases
     */
    private Type resolveReturnCastType(DescribedInvocation di){
        InterceptedInvocation interceptedInvocation = (InterceptedInvocation) di;
        InvocationOnMock impl = (InvocationOnMock) di;
        Method method = impl.getMethod();
        Type returnType = getActualType(method);
        Class<?> castClass = null;
        if(returnType != null){
            try {
                if (returnType instanceof Class) {
                    Class oldClazz = (Class) returnType;

                    // do Not handle primitive cast type
                    if(oldClazz.isPrimitive()) {
                        return null;
                    }

                    //cast needs target_class and line
                    Pattern pattern = Pattern.compile("-> at (.*?)\\(.*:(\\d+)\\)");
                    Matcher matcher = pattern.matcher(interceptedInvocation.getLocation().toString());

                    // locate location
                    Integer location = null;
                    String matcherClassName = null;
                    if (matcher.find()) {
                        matcherClassName = matcher.group(1).substring(0, matcher.group(1).lastIndexOf("."));
                        location = Integer.valueOf(matcher.group(2));
                    }

                    if (Properties.TARGET_CLASS.equals(matcherClassName)) {
                        //select class with location
                        castClass = CastClassManager.getInstance().selectCastClassWithLine(oldClazz, location);
                        //select class from variable types
                        if (castClass == null) {
                            castClass = CastClassManager.getInstance().selectCastClassFromVariable(oldClazz);
                        }
                    }
                }

            } catch (Exception ignore){
                LoggingUtils.getSmartUtLogger().debug("ignore exception for cast resolve");
            }
        }

        return castClass;
    }

    /**
     * get runtime return value
     * only when return value is Type Variable
     * @param method   method to be invoked
     * @return         runtime value
     */
    private Type getActualType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        Type actualReturnType = genericReturnType;
        if(genericReturnType instanceof TypeVariable) {
            Type exactType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.capture(retvalType.getType()),
                method.getDeclaringClass());
            if(exactType != null) {
                Map<TypeVariable<?>, Type> map = new LinkedHashMap<>();

                Type tmp = exactType;
                while (tmp instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) tmp;
                    Class<?> clazz = (Class<?>) paramType.getRawType(); // getRawType should always be Class
                    TypeVariable[] clazzRawTypeVariables = clazz.getTypeParameters();
                    Type[] actualTypes = paramType.getActualTypeArguments();
                    for(int i = 0; i < clazzRawTypeVariables.length; ++i) {
                        map.put(clazzRawTypeVariables[i], actualTypes[i]);
                    }
                    tmp = paramType.getOwnerType();
                }
                actualReturnType = map.get(genericReturnType);
            } else {
                actualReturnType = method.getReturnType();
            }
        }
        return actualReturnType;
    }

    /**
     * Analyze TypeVariable actual value in method return val
     * T method(T, others...)
     * T method(Class<T>, others...)
     * <T> List<T> method(Class<T>, others...)
     *
     * @param impl  InvocationOnMock, get value in runtime
     * @return      actual return type, return null if not belong to special cases
     */
    private Type typeParamReturnAnalyze(InvocationOnMock impl) {
        Method method = impl.getMethod();
        Type returnType = method.getGenericReturnType();

        // return type is T and param contains T
        if(returnType instanceof TypeVariable) {

            // Returns an array of Type objects
            Type[] paramsTypes = method.getGenericParameterTypes();
            boolean paramsContainsGenerics = false;

            // the param index which contain T compatible with return value
            int typeParamIndex = 0;
            for (int i = 0; i < paramsTypes.length && !paramsContainsGenerics; ++i) {

                // Two types, 1. parameterized type like Class<T>, List<T>, 2. type variable like T
                if(!(paramsTypes[i] instanceof ParameterizedType) && !(paramsTypes[i] instanceof TypeVariable)) {
                    continue;
                }

                // parameterized type
                if(paramsTypes[i] instanceof ParameterizedType) {
                    Type[] actualParamsTypes = ((ParameterizedType)paramsTypes[i]).getActualTypeArguments();
                    for (int j = 0; j < actualParamsTypes.length; ++j) {
                        // param is type variable and equals return type variable
                        if (actualParamsTypes[j] instanceof TypeVariable
                            && actualParamsTypes[j].getTypeName().equals(returnType.getTypeName())) {
                            paramsContainsGenerics = true;
                            typeParamIndex = i;
                            break;
                        }
                    }

                    if (paramsContainsGenerics) {
                        if (typeParamIndex < impl.getArguments().length) {
                            try {
                                // get runtime value
                                Class clazz = impl.getArgument(typeParamIndex);
                                return clazz;
                            }catch (Exception ignore) {
                                LoggingUtils.getSmartUtLogger().debug("ignore exception");
                            }

                            return null;
                        }
                    }
                } else {// TypeVariable
                    TypeVariable typeParam = (TypeVariable)paramsTypes[i];
                    // only when param type variable = return type variable
                    if(typeParam.getTypeName().equals(returnType.getTypeName())) {
                        if (typeParamIndex < impl.getArguments().length) {
                            try {
                                Class clazz = impl.getArgument(typeParamIndex).getClass();
                                // Notice，clazz here maybe mock object，e.g. com.example.OdcWorkflowOrder$MockitoMock$1352879938
                                // which dose NOT we want, we need real object
                                if(clazz.getName().contains("MockitoMock")) {
                                    String clazzRealName = clazz.getName().substring(0, clazz.getName().indexOf("$MockitoMock"));
                                    clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzRealName);
                                }
                                return clazz;
                            }catch (Exception ignore) {
                                LoggingUtils.getSmartUtLogger().debug("ignore exception here");
                            }

                            return null;
                        }
                    }
                }
            }
        }
        // return type is ParameterizedTypeImpl, e.g. List<T>
        else if(returnType instanceof ParameterizedTypeImpl) {
            // return value like List<T> contains single type variable，param is T
            Type tmp = returnType;
            Type[] actualTypeList = ((ParameterizedTypeImpl)tmp).getActualTypeArguments();
            if(actualTypeList.length != 1) {
                return null;
            }
            Type childType = ((ParameterizedTypeImpl)tmp).getActualTypeArguments()[0];
            if(childType instanceof TypeVariable) {
                // only handle T param
                Type[] paramsTypes = method.getGenericParameterTypes();
                boolean paramsContainsGenerics = false;
                int typeParamIndex = 0;
                for (int i = 0; i < paramsTypes.length && !paramsContainsGenerics; ++i) {
                    if(!(paramsTypes[i] instanceof ParameterizedType)) {
                        continue;
                    }
                    Type[] actualParamsTypes = ((ParameterizedTypeImpl)paramsTypes[i]).getActualTypeArguments();
                    for (int j = 0; j < actualParamsTypes.length; ++j) {
                        if (actualParamsTypes[j] instanceof TypeVariable) {
                            paramsContainsGenerics = true;
                            typeParamIndex = i;
                            break;
                        }
                    }

                    if (paramsContainsGenerics) {
                        if (typeParamIndex < impl.getArguments().length) {
                            Object obj = impl.getArgument(typeParamIndex);
                            if(obj instanceof Class) {
                                Class clazz = impl.getArgument(typeParamIndex);

                                // copy original type
                                Type[] updateActualTypeArguments = new Type[((ParameterizedType) tmp).getActualTypeArguments().length];
                                System.arraycopy(((ParameterizedType) tmp).getActualTypeArguments(), 0, updateActualTypeArguments, 0, ((ParameterizedType) tmp).getActualTypeArguments().length);
                                // update index 0 type directly because we
                                updateActualTypeArguments[0] = clazz;

                                // construct new return type
                                ParameterizedTypeImpl updateReturnType = ParameterizedTypeImpl.make(((ParameterizedTypeImpl) tmp).getRawType(), updateActualTypeArguments, ((ParameterizedTypeImpl) tmp).getOwnerType());
                                return updateReturnType;
                            }else{
                                return null;
                            }
                        }
                    }
                }
                return null;
            }
        }

        return null;
    }


    @Deprecated
    private MethodDescriptor getMethodDescriptor_old(DescribedInvocation di) {
    /*
        Current Mockito API seems quite limited. Here, to know what
        was called, it looks like the only way is to parse the results
        of toString.
        We can identify primitive types and String, but likely not the
        exact type of input objects. This is a problem if methods are overloaded
        and having same number of input parameters :(
     */
        String description = di.toString();

        int openingP = description.indexOf('(');
        assert openingP >= 0;

        String[] leftTokens = description.substring(0,openingP).split("\\.");
        String className = ""; //TODO
        String methodName = leftTokens[leftTokens.length-1];

        int closingP = description.lastIndexOf(')');
        String[] inputTokens = description.substring(openingP+1, closingP).split(",");

        String mockitoMatchers = "";
        if(inputTokens.length > 0) {
            /*
                TODO: For now it does not seem really feasible to infer the correct types.
                Left a feature request on Mockito mailing list, let's see if it ll be done
             */
            mockitoMatchers += "any()";
            for (int i=1; i<inputTokens.length; i++) {
                mockitoMatchers += " , any()";
            }
        }


        return new MethodDescriptor(className,methodName,mockitoMatchers);
    }
}
