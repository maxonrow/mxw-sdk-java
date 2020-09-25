package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"from", "itemID", "symbol", "to"})
public class NFTokenTransferItem {
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("itemID")
    private final String itemID;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;

    public NFTokenTransferItem(String symbol, String itemID) {
        this.symbol = symbol;
        this.itemID = itemID;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
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
