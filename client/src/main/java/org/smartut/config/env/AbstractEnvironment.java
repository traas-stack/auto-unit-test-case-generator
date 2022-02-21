package org.smartut.config.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class AbstractEnvironment implements ConfigurableEnvironment {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEnvironment.class);

    private final MutablePropertySources propertySources = new MutablePropertySources();

    public AbstractEnvironment() {
        customizePropertySources(this.propertySources);
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    @Override
    public Map<String, Object> getProperties(String resourceName) {
        return (Map) loadProperties(resourceName);
    }

    @Override
    public Map<String, Object> getMergedSource() {
        merge();
        return propertySources.mergedSource;
    }

    public void merge() {
        for (PropertySource propertySource : propertySources) {
            for (String key : propertySource.source.keySet()) {
                propertySources.mergedSource.put(key.replaceAll("\\.", "").toLowerCase(), propertySource.getProperty(key));
            }
        }
    }

    private Properties loadProperties(String resourceName) {
        Properties props = new Properties();
        ClassLoader classLoader = getDefaultClassLoader();
        if (classLoader == null){
            logger.error("ClassLoader is null, Skipped missing config " + resourceName);
            return props;
        }
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null) {
            logger.warn("Skipped missing config " + resourceName);
            return props;
        }
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {
            logger.debug("Cannot access thread context ClassLoader - falling back...");
        }
        if (cl == null) {
            logger.debug("No thread context class loader -> use class loader of this class.");
            cl = AbstractEnvironment.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    logger.error("Cannot access system ClassLoader");
                }
            }
        }
        return cl;
    }


}
