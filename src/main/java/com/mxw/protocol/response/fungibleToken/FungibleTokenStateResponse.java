package com.mxw.protocol.response.fungibleToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FungibleTokenStateResponse {
    public String value;
}
