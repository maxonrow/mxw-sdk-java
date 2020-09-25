package com.mxw.protocol.response.nonFungibleToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NFTokenStateResponse {
    public String value;
}
