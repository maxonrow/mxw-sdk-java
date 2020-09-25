package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"fee", "metadata", "name", "owner", "properties", "symbol"})
public class NFTokenCreate {
    @JsonProperty("metadata")
    private final String metadata;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("symbol")
    private final String symbol;
    @JsonProperty("properties")
    private final String properties;
    @JsonProperty("fee")
    private final Fee fee = new Fee();

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

    public NFTokenCreate(String appFeeTo, String appFeeValue, String name, String metadata,
                         String properties, String symbol) {
        this.fee.to = appFeeTo;
        this.fee.value = appFeeValue;
        this.name = name;
        this.metadata = metadata != null ? metadata : "";
        this.symbol = symbol;
        this.properties = properties;
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

    public String getProperties() {
        return this.properties;
    }

    public Fee getFee() {
        return this.fee;
    }


}
