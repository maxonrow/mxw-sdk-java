package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

import java.util.List;

@JsonPropertyOrder({"pub_key", "signature", "token"})
public class NFTokenStatusPayload {
    @JsonProperty("token")
    public NFToken token = new NFToken();
    @JsonProperty("pub_key")
    public PublicKey pub_key = new PublicKey();
    @JsonProperty("signature")
    public String signature;

    @JsonPropertyOrder({"burnable", "endorserList", "endorserListLimit", "from", "mintLimit", "modifiable", "nonce", "pub", "status", "symbol", "tokenFees", "transferLimit", "transferable"})
    public static class NFToken {
        @JsonProperty("from")
        public String from;
        @JsonProperty("nonce")
        public String nonce;
        @JsonProperty("status")
        public String status;
        @JsonProperty("symbol")
        public String symbol;
        @JsonProperty("transferLimit")
        public String transferLimit;
        @JsonProperty("mintLimit")
        public String mintLimit;
        @JsonProperty("endorserListLimit")
        public String endorserListLimit;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("tokenFees")
        public List<NFTokenStatusFee> tokenFees;
        @JsonProperty("endorserList")
        public List<String> endorserList;
        @JsonProperty("burnable")
        public boolean burnable;
        @JsonProperty("transferable")
        public boolean transferable;
        @JsonProperty("modifiable")
        public boolean modifiable;
        @JsonProperty("pub")
        public boolean pub;
    }
}
