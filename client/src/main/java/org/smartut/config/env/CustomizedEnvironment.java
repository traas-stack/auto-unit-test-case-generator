package org.smartut.config.env;

public class CustomizedEnvironment extends StandardEnvironment{

    public static final String CUSTOMIZE_PROPERTIES_PROPERTY_SOURCE_NAME = "customizedProperties";

    public static final String CUSTOMIZE_PROPERTIES_PROPERTY_SOURCE_PATH = "config/customized.properties";

    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addFirst(new PropertySource(CUSTOMIZE_PROPERTIES_PROPERTY_SOURCE_NAME, getProperties(CUSTOMIZE_PROPERTIES_PROPERTY_SOURCE_PATH)));
        super.customizePropertySources(propertySources);
    }


}
