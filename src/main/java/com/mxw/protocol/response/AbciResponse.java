package com.mxw.protocol.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxw.protocol.deserializer.Base64Deserializer;

public class AbciResponse<T> {

    @JsonDeserialize(using = Base64Deserializer.class)
    private T value;

    private String height;

    public AbciResponse() {}

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
