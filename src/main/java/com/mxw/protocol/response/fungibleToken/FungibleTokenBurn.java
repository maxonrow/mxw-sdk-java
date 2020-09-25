package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"from", "symbol", "value"})
public class FungibleTokenBurn {
    @JsonProperty("value")
    private final String value;
    private final String symbol;
    private String from;

    public FungibleTokenBurn(String symbol, String value) {
        this.symbol = symbol;
        this.value = value;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getValue() {
        return this.value;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

}
