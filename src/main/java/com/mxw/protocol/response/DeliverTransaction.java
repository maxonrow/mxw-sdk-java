package com.mxw.protocol.response;

import java.math.BigInteger;

public class DeliverTransaction {

    public DeliverTransaction(){

    }

    private String hash;

    private TransactionLog log;

    private BigInteger nonce;

    private BigInteger gasUsed;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public TransactionLog getLog() {
        return log;
    }

    public void setLog(TransactionLog log) {
        this.log = log;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }
}
