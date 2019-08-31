package com.mxw.protocol.response;

import com.mxw.utils.Numeric;

public class AliasState {

    public AliasState(){

    }

    private String name;

    private Boolean approved;

    private String owner;

    private String metadata;

    private Numeric fee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Numeric getFee() {
        return fee;
    }

    public void setFee(Numeric fee) {
        this.fee = fee;
    }
}
