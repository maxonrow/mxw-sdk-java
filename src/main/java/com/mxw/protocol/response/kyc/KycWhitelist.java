package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KycWhitelist {

    private KycData kycData;
    private String kycOwner;

    public KycWhitelist(KycData kycData, String kycOwner){
        this.setKycData(kycData);
        this.setKycOwner(kycOwner);
    }

    @JsonProperty("kycData")
    public KycData getKycData() {
        return kycData;
    }

    public void setKycData(KycData kycData) {
        this.kycData = kycData;
    }

    @JsonProperty("owner")
    public String getKycOwner() {
        return kycOwner;
    }

    public void setKycOwner(String kycOwner) {
        this.kycOwner = kycOwner;
    }
}
