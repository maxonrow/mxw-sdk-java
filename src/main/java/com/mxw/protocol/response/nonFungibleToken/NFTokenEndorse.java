package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"from", "itemID", "metadata", "symbol"})
public class NFTokenEndorse {
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("from")
    private String from;
    @JsonProperty("itemID")
    private final String itemID;
    @JsonProperty("metadata")
    private String metadata;

    public NFTokenEndorse(String symbol, String itemID, String metadata) {
        this.symbol = symbol;
        this.itemID = itemID;
        this.metadata = metadata;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getItemID() {
        return this.itemID;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }


}
