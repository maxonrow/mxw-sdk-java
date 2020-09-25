package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

@JsonPropertyOrder({"kyc", "pub_key","signature"})
public class KycWhitelistPayload {

    private KycWhitelistModel kyc;
    private PublicKey publicKey;
    private String signature;

    public KycWhitelistPayload(KycWhitelistModel kycWhitelistModel, PublicKey pubKey, String signature){
        this.setKycWhitelistModel(kycWhitelistModel);
        this.setPubKey(pubKey);
        this.setSignature(signature);
    }

    @JsonProperty("kyc")
    public KycWhitelistModel getKycWhitelistModel() {
        return kyc;
    }

    public void setKycWhitelistModel(KycWhitelistModel kyc) {
        this.kyc = kyc;
    }

    @JsonProperty("pub_key")
    public PublicKey getPubKey() {
        return publicKey;
    }

    public void setPubKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
