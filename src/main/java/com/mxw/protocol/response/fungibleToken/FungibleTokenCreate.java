package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"decimals", "fee", "fixedSupply", "maxSupply", "metadata", "name", "owner", "symbol"})
public class FungibleTokenCreate {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("decimals")
    private final String decimals;
    @JsonProperty("fixedSupply")
    private final boolean fixedSupply;
    @JsonProperty("maxSupply")
    private final String maxSupply;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("fee")
    private final Fee fee = new Fee();
    @JsonProperty("metadata")
    private final String metadata;

    @JsonPropertyOrder({"to", "value"})
    public static class Fee {
        @JsonProperty("to")
        String to;
        @JsonProperty("value")
        String value;

        public String getTo() {
            return this.to;
        }

        public String getValue() {
            return this.value;
        }
    }

    public FungibleTokenCreate(String appFeeTo, String appFeeValue, String name, String metadata,
                               String symbol, String decimals, boolean fixedSupply, String maxSupply) {
        this.fee.to = appFeeTo;
        this.fee.value = appFeeValue;
        this.name = name;
        this.decimals = decimals;
        this.metadata = metadata != null ? metadata : "";
        this.symbol = symbol;
        this.fixedSupply = fixedSupply;
        this.maxSupply = maxSupply;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Fee getFee() {
        return this.fee;
    }


}
