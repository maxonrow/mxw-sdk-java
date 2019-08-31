package com.mxw.protocol.response;

import com.mxw.utils.Numeric;

public class TokenState {

    public TokenState(){

    }

    private String type;

    private String name;

    private String symbol;

    private Integer decimals;

    private Boolean fixedSupply;

    private Numeric totalSupply;

    private Boolean approved;

    private Boolean frozen;

    private String owner;

    private String metadata;

    private Numeric transferFee;

    private Boolean burnable;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Boolean getFixedSupply() {
        return fixedSupply;
    }

    public void setFixedSupply(Boolean fixedSupply) {
        this.fixedSupply = fixedSupply;
    }

    public Numeric getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(Numeric totalSupply) {
        this.totalSupply = totalSupply;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
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

    public Numeric getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(Numeric transferFee) {
        this.transferFee = transferFee;
    }

    public Boolean getBurnable() {
        return burnable;
    }

    public void setBurnable(Boolean burnable) {
        this.burnable = burnable;
    }
}
