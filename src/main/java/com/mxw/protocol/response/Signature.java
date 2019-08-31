package com.mxw.protocol.response;

import com.mxw.protocol.response.PublicKey;

public class Signature {

    private PublicKey publicKey;

    private String signature;

    public Signature() {

    }

    public Signature(PublicKey publicKey, String signature) {
        this.publicKey = publicKey;
        this.signature = signature;
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
