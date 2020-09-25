package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.fungibleToken.FungibleTokenEnum;
import com.mxw.nonFungibleToken.NonFungibleTokenEnum;

@JsonPropertyOrder({"action", "feeName"})
public class FungibleTokenStatusFee {
    @JsonProperty("action")
    public String action;
    @JsonProperty("feeName")
    public String feeName;

    public FungibleTokenStatusFee() {
    }

    public FungibleTokenStatusFee(String action, String feeName) {
        this.action = action;
        this.feeName = feeName;
    }

    public FungibleTokenStatusFee(FungibleTokenEnum.FungibleTokenActions actionEnum, String feeName) {
        this.action = actionEnum.toString();
        this.feeName = feeName;
    }
}
