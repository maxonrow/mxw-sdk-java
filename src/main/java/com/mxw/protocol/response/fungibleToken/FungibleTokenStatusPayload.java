package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mxw.protocol.response.PublicKey;

import java.util.List;

@JsonPropertyOrder({"pub_key", "signature", "token"})
public class FungibleTokenStatusPayload {
    @JsonProperty("token")
    public FungibleTokenSetting token = new FungibleTokenSetting();
    @JsonProperty("pub_key")
    public PublicKey pub_key = new PublicKey();
    @JsonProperty("signature")
    public String signature;

    @JsonPropertyOrder({"burnable", "from", "nonce", "status", "symbol", "tokenFees"})
    public static class FungibleTokenSetting {
        @JsonProperty("from")
        public String from;
        @JsonProperty("nonce")
        public String nonce;
        @JsonProperty("status")
        public String status;
        @JsonProperty("symbol")
        public String symbol;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("tokenFees")
        public List<FungibleTokenStatusFee> tokenFees;
        @JsonProperty("burnable")
        public boolean burnable;
    }
}
