package com.mxw.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

@JsonPropertyOrder({"pub_key","signature"})
public class Signature {

    private PublicKey publicKey;

    private String signature;

    public Signature() {

    }

    public Signature(PublicKey publicKey, String signature) {
        this.publicKey = publicKey;
        this.signature = signature;
    }

    @JsonProperty("pub_key")
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
