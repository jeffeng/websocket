package com.anglele.config;

import java.util.*;

/**
 * Created by jeffeng on 2017/8/12.
 */
public class OrderedProperties extends Properties {
    private static final long serialVersionUID = -4627607243846121965L;
    private final LinkedHashSet<Object> keys = new LinkedHashSet();

    public OrderedProperties() {
    }

    public Enumeration<Object> keys() {
        return Collections.enumeration(this.keys);
    }

    public Object put(Object key, Object value) {
        this.keys.add(key);
        return super.put(key, value);
    }

    public Set<Object> keySet() {
        return this.keys;
    }

    public Set<String> stringPropertyNames() {
        LinkedHashSet set = new LinkedHashSet();
        Iterator var2 = this.keys.iterator();

        while (var2.hasNext()) {
            Object key = var2.next();
            set.add((String) key);
        }

        return set;
    }
}
