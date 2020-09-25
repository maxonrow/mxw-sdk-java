package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.Signature;
import com.mxw.protocol.response.nonFungibleToken.NFTokenStatusPayload;

import java.util.ArrayList;

@JsonPropertyOrder({"owner", "payload", "signature"})
public class FungibleTokenStatusTransaction {
    @JsonProperty("owner")
    public String owner;
    @JsonProperty("signatures")
    public ArrayList<Signature> signatures = new ArrayList<>();
    @JsonProperty("payload")
    public final FungibleTokenStatusPayload payload;

    public FungibleTokenStatusTransaction(FungibleTokenStatusPayload payload) {
        this.payload = payload;
    }
}
