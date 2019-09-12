package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

@JsonPropertyOrder({"kyc", "pub_key","signature"})
public class KycWhitelistPayload {

    private KycWhitelistModel kycWhitelistModel;
    private PublicKey pubKey;
    private String signature;

    public KycWhitelistPayload(KycWhitelistModel kycWhitelistModel, PublicKey pubKey, String signature){
        this.setKycWhitelistModel(kycWhitelistModel);
        this.setPubKey(pubKey);
        this.setSignature(signature);
    }

    @JsonProperty("kyc")
    public KycWhitelistModel getKycWhitelistModel() {
        return kycWhitelistModel;
    }

    public void setKycWhitelistModel(KycWhitelistModel kycWhitelistModel) {
        this.kycWhitelistModel = kycWhitelistModel;
    }

    @JsonProperty("pub_key")
    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
