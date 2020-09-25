package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"from", "itemID", "symbol"})
public class NFTokenBurn {
    @JsonProperty("itemID")
    private final String itemID;
    private final String symbol;
    private String from;

    public NFTokenBurn(String symbol, String itemID) {
        this.symbol = symbol;
        this.itemID = itemID;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getItemID() {
        return this.itemID;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

}
