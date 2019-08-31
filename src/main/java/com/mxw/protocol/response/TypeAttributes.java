package com.mxw.protocol.response;

import java.util.List;

public class TypeAttributes {

    private String type;

    private List<Attribute> attributes;


    public TypeAttributes() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }


    public static class Attribute {

        private String key;

        private Object value;

        public Attribute() {

        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}
