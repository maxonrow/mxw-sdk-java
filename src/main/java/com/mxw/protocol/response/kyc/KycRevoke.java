package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.Signature;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"owner", "payload", "signatures"})
public class KycRevoke {

    private String kycOwner;
    private KycRevokePayload kycRevokePayload;
    private List<Signature> signatureList;

    public KycRevoke(String kycOwner, KycRevokePayload kycRevokePayload, Signature signature){
        this.setKycOwner(kycOwner);
        this.setKycRevokePayload(kycRevokePayload);

        signatureList = new ArrayList<>();
        signatureList.add(signature);

        this.setSignatureList(signatureList);
    }

    @JsonProperty("owner")
    public String getKycOwner() {
        return kycOwner;
    }

    public void setKycOwner(String kycOwner) {
        this.kycOwner = kycOwner;
    }

    @JsonProperty("payload")
    public KycRevokePayload getKycRevokePayload() {
        return kycRevokePayload;
    }

    public void setKycRevokePayload(KycRevokePayload kycRevokePayload) {
        this.kycRevokePayload = kycRevokePayload;
    }

    @JsonProperty("signatures")
    public List<Signature> getSignatureList() {
        return signatureList;
    }

    public void setSignatureList(List<Signature> signatureList) {
        this.signatureList = signatureList;
    }
}
