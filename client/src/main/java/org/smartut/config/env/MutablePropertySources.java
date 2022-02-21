package org.smartut.config.env;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MutablePropertySources implements Iterable<PropertySource> {

    private final List<PropertySource> propertySourceList = new CopyOnWriteArrayList<>();

    protected Map<String, Object> mergedSource = new HashMap<>();

    @Override
    public Iterator<PropertySource> iterator() {
        return this.propertySourceList.iterator();
    }

    public void addLast(PropertySource propertySource) {
        removeIfPresent(propertySource);
        this.propertySourceList.add(propertySource);
    }

    public void addFirst(PropertySource propertySource) {
        removeIfPresent(propertySource);
        this.propertySourceList.add(0, propertySource);
    }

    protected void removeIfPresent(PropertySource propertySource) {
        this.propertySourceList.remove(propertySource);
    }


}
