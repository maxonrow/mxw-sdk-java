package com.mxw.protocol.response.kyc;

import java.math.BigInteger;

public class KycRevokeModel {

    private String from;
    private BigInteger nonce;
    private String to;

    public KycRevokeModel(String from, BigInteger nonce, String to){
        this.setFrom(from);
        this.setNonce(nonce);
        this.setTo(to);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

