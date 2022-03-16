package org.smartut.config.env;

public class StandardEnvironment extends AbstractEnvironment{

    public static final String STANDARD_PROPERTIES_PROPERTY_SOURCE_NAME = "standardProperties";

    public static final String STANDARD_PROPERTIES_PROPERTY_SOURCE_PATH = "config/standard.properties";

    protected void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addFirst(new PropertySource(STANDARD_PROPERTIES_PROPERTY_SOURCE_NAME, getProperties(STANDARD_PROPERTIES_PROPERTY_SOURCE_PATH)));
    }
}
