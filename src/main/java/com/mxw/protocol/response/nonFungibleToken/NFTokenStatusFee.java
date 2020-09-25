package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.nonFungibleToken.NonFungibleTokenEnum;

@JsonPropertyOrder({"action", "feeName"})
public class NFTokenStatusFee {
    @JsonProperty("action")
    public String action;
    @JsonProperty("feeName")
    public String feeName;

    public NFTokenStatusFee() {
    }

    public NFTokenStatusFee(String action, String feeName) {
        this.action = action;
        this.feeName = feeName;
    }

    public NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions actionEnum, String feeName) {
        this.action = actionEnum.toString();
        this.feeName = feeName;
    }
}
