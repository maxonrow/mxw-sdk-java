package com.mxw.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigInteger;

public class InnerResponse {

    // zero = success
    private int code;

    private String log;

    private String codespace;

    private JsonNode response;

    private String hash;

    @JsonProperty("blockNumber")
    private BigInteger blockNumber;


    public InnerResponse(){
        this(0, "","");
    }

    public InnerResponse(int code) {
        this(code, "Unknown Error","sdk");
    }

    public InnerResponse(int code, String log, String codespace) {
        this.code = code;
        this.log = log;
        this.codespace = codespace;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getCodespace() {
        return codespace;
    }

    public void setCodespace(String codespace) {
        this.codespace = codespace;
    }

    public JsonNode getResponse() {
        return response;
    }

    public void setResponse(JsonNode response) {
        this.response = response;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }
}
