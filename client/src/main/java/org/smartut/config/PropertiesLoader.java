package org.smartut.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class PropertiesLoader {

    protected final static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    protected void fieldSetValue(Field field, Object valueObj) throws IllegalAccessException {
        if (valueObj instanceof String){
            String valueStr = (String) valueObj;
            if (StringUtils.isBlank(valueStr)){
                return;
            }
            Class<?> type = field.getType();
            if (type.isArray()){
                Class<?> componentType = type.getComponentType();
                if (!componentType.equals(String.class)){
                    logger.warn("Config error, the properties array only support string type array: " + field.getName().toLowerCase());
                    return;
                }
                valueObj = valueStr.split(",");
            }else {
                valueObj = strToObj(type, valueStr);
            }
        }
        field.set(this, valueObj);
    }

    protected Object strToObj(Class<?> type, String str){
        Object obj = str;
        if (type.equals(Integer.class)){
            obj = Integer.valueOf(str);
        }else if (type.equals(int.class)){
            obj = Integer.parseInt(str);
        }else if (type.equals(Long.class)){
            obj = Long.valueOf(str);
        }else if (type.equals(long.class)){
            obj = Long.parseLong(str);
        }else if (type.equals(Double.class)){
            obj = Double.valueOf(str);
        }else if (type.equals(double.class)){
            obj = Double.parseDouble(str);
        }else if (type.equals(Boolean.class)){
            obj = Boolean.valueOf(str);
        }else if (type.equals(boolean.class)){
            obj = Boolean.parseBoolean(str);
        }
        return obj;
    }

    /**
     * This exception is used when a non-existent parameter is accessed
     */
    public static class NoSuchParameterException extends Exception {

        private static final long serialVersionUID = 9074828392047742535L;

        public NoSuchParameterException(String key) {
            super("No such property defined: " + key);
        }
    }
}
