package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.Signature;

import java.util.ArrayList;

@JsonPropertyOrder({"owner", "payload", "signature"})
public class NFTokenStatusTransaction {
    @JsonProperty("owner")
    public String owner;
    @JsonProperty("signatures")
    public ArrayList<Signature> signatures = new ArrayList<>();
    @JsonProperty("payload")
    public final NFTokenStatusPayload payload;

    public NFTokenStatusTransaction(NFTokenStatusPayload payload) {
        this.payload = payload;
    }
}
