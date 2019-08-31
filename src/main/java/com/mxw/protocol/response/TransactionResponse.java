package com.mxw.protocol.response;

public class TransactionResponse {

    private String hash;

    public TransactionResponse(){

    }

    public TransactionResponse(String hash) {
        this.hash= hash;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
