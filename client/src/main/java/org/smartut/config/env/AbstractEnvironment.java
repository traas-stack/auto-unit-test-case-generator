package org.smartut.config.env;

import org.smartut.config.PropertiesLoader;

import java.util.Map;

public class AbstractEnvironment implements ConfigurableEnvironment {

    private final MutablePropertySources propertySources = new MutablePropertySources();

    public AbstractEnvironment() {
        customizePropertySources(this.propertySources);
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    @Override
    public Map<String, Object> getProperties(String resourceName) {
        return (Map) PropertiesLoader.loadPropertiesFile(resourceName);
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

}
