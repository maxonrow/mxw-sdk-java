package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"owner", "symbol", "to", "value"})
public class FungibleTokenMint {
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("value")
    private final String value;
    @JsonProperty("to")
    private String to;
    @JsonProperty("owner")
    private String owner;

    public FungibleTokenMint(String symbol, String value) {
        this.symbol = symbol;
        this.value = value;
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

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
