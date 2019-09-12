package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxw.protocol.response.Signature;

import java.util.ArrayList;
import java.util.List;

public class KycData {

    private KycWhitelistPayload kycWhitelistPayload;
    private List<Signature> signatureList;

    public KycData(KycWhitelistPayload kycWhitelistPayload){
        this.setKycWhitelistPayload(kycWhitelistPayload);
        this.signatureList = new ArrayList<>();
    }

    @JsonProperty("payload")
    public KycWhitelistPayload getKycWhitelistPayload() {
        return kycWhitelistPayload;
    }

    public void setKycWhitelistPayload(KycWhitelistPayload kycWhitelistPayload) {
        this.kycWhitelistPayload = kycWhitelistPayload;
    }

    @JsonProperty("signatures")
    public List<Signature> getSignatureList() {
        return signatureList;
    }

    public void setSignatureList(List<Signature> signatureList) {
        this.signatureList = signatureList;
    }
}

