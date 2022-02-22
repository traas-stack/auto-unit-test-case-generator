package org.smartut.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.utils.LoggingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @description:  Properties Loader
 * @author: Ding
 */
public class PropertiesLoader {

    protected final static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    protected void fieldSetValue(Field field, Object valueObj) throws IllegalAccessException {
        if (valueObj instanceof String){
            String valueStr = (String) valueObj;
            if (StringUtils.isBlank(valueStr)){
                return;
            }
            Class<?> type = field.getType();
            if (type.isEnum()){
                field.set(null, Enum.valueOf((Class<Enum>) field.getType(), valueStr.toUpperCase()));
            } else if (type.isArray()){
                Class<?> componentType = type.getComponentType();
                valueObj = Stream.of(valueStr.split(":")).map(x -> strToObj(componentType, x)).toArray();
            }else {
                valueObj = strToObj(type, valueStr);
            }
        }
        field.set(this, valueObj);
    }

    protected Object strToObj(Class<?> type, String str){
        Object obj = str;
        //Integers
        if (type.equals(Integer.class)){
            obj = Integer.valueOf(str);
        }else if (type.equals(int.class)){
            obj = Integer.parseInt(str);
        //Long
        }else if (type.equals(Long.class)){
            obj = Long.valueOf(str);
        }else if (type.equals(long.class)){
            obj = Long.parseLong(str);
        //Double
        }else if (type.equals(Double.class)){
            obj = Double.valueOf(str);
        }else if (type.equals(double.class)){
            obj = Double.parseDouble(str);
        //Boolean
        }else if (type.equals(Boolean.class)){
            obj = Boolean.valueOf(str);
        }else if (type.equals(boolean.class)){
            obj = Boolean.parseBoolean(str);
        }else if (type.equals(org.smartut.Properties.Criterion.class)){
            obj = Enum.valueOf(org.smartut.Properties.Criterion.class, str.toUpperCase());
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

    /**
     * Load a properties file
     *
     * @param propertiesPath
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Properties} object.
     */
    public static Properties loadPropertiesFile(String propertiesPath) {
        return loadPropertiesFile(propertiesPath, true);
    }

    /**
     * Load a properties file
     *
     * @param propertiesPath
     *            a {@link java.lang.String} object.
     * @param silent
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.Properties} object.
	 */
    public static Properties loadPropertiesFile(String propertiesPath, boolean silent) {
        Properties properties = new java.util.Properties();
        try {
            InputStream in;
            File propertiesFile = new File(propertiesPath);
            if (propertiesFile.exists()) {
                in = new FileInputStream(propertiesPath);
                properties.load(in);
                if (!silent){
                    LoggingUtils.getSmartUtLogger().info("* Properties loaded from "
                            + propertiesFile.getAbsolutePath());
                }
            } else {
                in = PropertiesLoader.class.getClass().getClassLoader().getResourceAsStream(propertiesPath);
                if (in != null) {
                    properties.load(in);
                    if (!silent){
                        LoggingUtils.getSmartUtLogger().info("* Properties loaded from "
                                + PropertiesLoader.class.getClass().getClassLoader().getResource(propertiesPath).getPath());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("- Error: Could not find configuration file " + propertiesPath);
        }
        return properties;
    }

}
