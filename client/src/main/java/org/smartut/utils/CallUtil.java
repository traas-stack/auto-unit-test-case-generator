package org.smartut.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Call pointcuts util
 */
public class CallUtil {

    private static final Logger logger = LoggerFactory.getLogger(CallUtil.class);

    public static <T> T call(String className, String methodName, Object... args) {
        try {
            Class<?> caller = Class.forName(className);
            Object callRes = CallUtil.call(caller, methodName, args);
            if (callRes != null){
                return (T) callRes;
            }
        } catch (Exception e) {
            logger.error("call className-" + className + " , method-" + methodName + " error：" + e.getMessage());
        }
        return null;
    }

    public static Object call(Class<?> caller, String methodName, Object... args) {
        try {
            Method m;
            if (args != null && args.length > 0) {
                Class<?>[] parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
                try {
                    m = caller.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    Method[] declaredMethods = caller.getDeclaredMethods();
                    for (Method declaredMethod : declaredMethods) {
                        Class<?>[] standardParameterTypes = declaredMethod.getParameterTypes();
                        if (isSameMethod(standardParameterTypes, parameterTypes)) {
                            return declaredMethod.invoke(caller.newInstance(), args);
                        }
                    }
                    return null;
                }
            } else {
                m = caller.getDeclaredMethod(methodName);
            }
            return m.invoke(caller.newInstance(), args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error while " + caller.getName() + " call method " + methodName + " , " +
                    e.getMessage(), e);
        }
        return null;
    }


    private static boolean isSameMethod(Class<?>[] standardParameterTypes, Class<?>[] parameterTypes) {
        if (standardParameterTypes.length != parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < standardParameterTypes.length; i++) {
            if (!standardParameterTypes[i].isAssignableFrom(parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

}
