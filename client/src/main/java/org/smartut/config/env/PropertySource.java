package org.smartut.config.env;

import java.util.Map;

public class PropertySource {

    protected final String name;

    protected final Map<String, Object> source;

    public PropertySource(String name, Map<String, Object> source) {
        this.name = name;
        this.source = source;
    }

    public Object getProperty(String name) {
        return this.source.get(name);
    }

    public boolean containsProperty(String name) {
        return this.source.containsKey(name);
    }

    public String[] getPropertyNames() {
        return this.source.keySet().toArray(new String[this.source.size()]);
    }

}
