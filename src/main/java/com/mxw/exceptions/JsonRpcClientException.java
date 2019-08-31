package com.mxw.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonRpcClientException extends RuntimeException {

    private final int code;

    /**
     * Creates the exception.
     *
     * @param code    the code from the server
     * @param message the message from the server
     */
    public JsonRpcClientException(int code, String message) {
        super(message);
        this.code = code;
       // this.data = data;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }



}
