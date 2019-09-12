package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

@JsonPropertyOrder({"kyc", "pub_key","signature"})
public class KycRevokePayload {

    private KycRevokeModel kycRevokeModel;
    private PublicKey pubKey;
    private String signature;

    public KycRevokePayload(KycRevokeModel kycRevoketModel, PublicKey pubKey, String signature){
        this.setKycRevokeModel(kycRevoketModel);
        this.setPubKey(pubKey);
        this.setSignature(signature);
    }

    @JsonProperty("kyc")
    public KycRevokeModel getKycRevokeModel() {
        return kycRevokeModel;
    }

    public void setKycRevokeModel(KycRevokeModel kycRevokeModel) {
        this.kycRevokeModel = kycRevokeModel;
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
