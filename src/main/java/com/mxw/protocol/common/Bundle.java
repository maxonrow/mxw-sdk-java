package com.mxw.protocol.common;

import java.util.HashMap;

public class Bundle extends HashMap<String, Object> {

    public Bundle put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public boolean contain(String key) {
        return super.containsKey(key);
    }

    public Object get(String key) {
        return super.get(key);
    }

    public <T> T get(String key, T defaultValue) {
        if (!super.containsKey(key))
            return defaultValue;
        return (T) super.get(key);
    }

}
