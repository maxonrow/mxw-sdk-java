package com.mxw.protocol.response;


public class TransactionMessage<T> {

    public TransactionMessage(){

    }

    private String type;

    private T value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
