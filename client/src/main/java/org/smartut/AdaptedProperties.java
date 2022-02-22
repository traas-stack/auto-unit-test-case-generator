package org.smartut;

import org.smartut.config.AbstractProperties;

/**
 * Adaptation configuration for personalized function extension
 *
 * @author Ding
 */
public class AdaptedProperties extends AbstractProperties {

    @Properties.Parameter(key = "version", group = "AdapterPlugin", description = "Project version; defaults opensource")
    public static String VERSION = "opensource";

    @Properties.Parameter(key = "code_analysis_plugins", group = "AdapterPlugin", description = "Code analysis phase plugins class")
    public static String[] CODE_ANALYSIS_PLUGINS;

    @Properties.Parameter(key = "statement_plugin", group = "AdapterPlugin", description = "Json statement plugins class")
    public static String STATEMENT_PLUGIN;


    @Override
    protected void setProperties(){
        setProperties(AdaptedProperties.class);
    }



}
