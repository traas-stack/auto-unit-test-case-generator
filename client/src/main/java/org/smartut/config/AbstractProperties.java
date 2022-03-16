package org.smartut.config;

import org.smartut.config.env.AbstractEnvironment;
import org.smartut.config.env.CustomizedEnvironment;

import java.lang.reflect.Field;
import java.util.Map;

public class AbstractProperties extends PropertiesLoader {

    protected AbstractEnvironment environment;

    public AbstractProperties() {
        loadProperties();
    }

    public void loadProperties() {
        prepareEnvironment();
        setProperties();
    }

    protected void setProperties() {
    }

    protected void setProperties(Class<? extends AbstractProperties> clazz){
        Map<String, Object> mergedSource = environment.getMergedSource();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (mergedSource.containsKey(field.getName().toLowerCase())){
                try {
                    fieldSetValue(field, mergedSource.get(field.getName().toLowerCase()));
                } catch (IllegalAccessException e) {
                    logger.warn("Config error: " + field.getName().toLowerCase());
                }
            }
        }
    }

    private void prepareEnvironment() {
        if (environment == null) {
            environment = new CustomizedEnvironment();
        }
    }

}
