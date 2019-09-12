package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;

@JsonPropertyOrder({"from", "kycAddress", "nonce"})
public class KycWhitelistModel {

    private String from;

    private String kycAddress;

    private BigInteger nonce;

    public KycWhitelistModel(String from, String kycAddress, BigInteger nonce){
        this.from = from;
        this.kycAddress = kycAddress;
        this.nonce = nonce;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("kycAddress")
    public String getKycAddress() {
        return kycAddress;
    }

    public void setKycAddress(String kycAddress) {
        this.kycAddress = kycAddress;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }
}

