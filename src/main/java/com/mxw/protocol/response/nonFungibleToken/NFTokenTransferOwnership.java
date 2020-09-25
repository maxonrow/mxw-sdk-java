package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"from", "symbol", "to"})
public class NFTokenTransferOwnership {
    private String symbol;
    private String from;
    private String to;

    public NFTokenTransferOwnership(String symbol, String from, String to) {
        setSymbol(symbol);
        setFrom(from);
        setTo(to);
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
