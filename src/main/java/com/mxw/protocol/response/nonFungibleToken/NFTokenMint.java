package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"itemID", "metadata", "owner", "properties", "symbol", "to"})
public class NFTokenMint {
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("itemID")
    private final String itemID;
    @JsonProperty("properties")
    private final String properties;
    @JsonProperty("metadata")
    private final String metadata;
    @JsonProperty("to")
    private String to;
    @JsonProperty("owner")
    private String owner;

    public NFTokenMint(String symbol, String itemID,
                       String properties, String metadata) {
        this.symbol = symbol;
        this.itemID = itemID;
        this.properties = properties;
        this.metadata = metadata;
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

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProperties() {
        return this.properties;
    }

    public String getMetadata() {
        return this.metadata;
    }


}
