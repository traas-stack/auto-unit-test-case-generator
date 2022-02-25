package org.smartut.config.env;

import java.util.Map;

/**
 * ConfigurableEnvironment serve as the building blocks for plugins configuring class loading
 *
 * author: Ding
 */
public interface ConfigurableEnvironment {

    /**
     * description: Get properties from resource file
     * @param resourceName resource file name
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getProperties(String resourceName);

    /**
     * description: Get the properties after merged
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getMergedSource();

}
