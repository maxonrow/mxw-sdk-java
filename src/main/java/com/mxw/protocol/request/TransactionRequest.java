package com.mxw.protocol.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mxw.protocol.response.TransactionValue;

import java.math.BigInteger;

public class TransactionRequest {

    private String type;

    @JsonIgnore
    private BigInteger nonce;
    @JsonIgnore
    private BigInteger accountNumber;
    @JsonIgnore
    private String chainId;
    @JsonIgnore
    private BigInteger sequence;

    private TransactionValue value;

    public TransactionRequest() {
        super();
    }

    public TransactionValue getValue() {
        return value;
    }

    public void setValue(TransactionValue value) {
        this.value = value;
    }


    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(BigInteger accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(BigInteger sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
