package com.mxw.protocol.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxw.protocol.deserializer.BigIntegerDeserializer;
import com.mxw.protocol.deserializer.PayloadDeserializer;

import java.math.BigInteger;
import java.util.List;

public class TransactionReceipt<T> {

    @JsonProperty("blockNumber")
    private BigInteger blockNumber;

    private DeliverTransaction deliverTransaction;

    private String hash;

    private Result result;

    @JsonDeserialize(using= BigIntegerDeserializer.class)
    private BigInteger nonce;

    private Integer status;

    @JsonDeserialize(using = PayloadDeserializer.class)
    @JsonAlias({"tx","payload"})
    private T payload;

    private String rawPayload;


    public TransactionReceipt(){

    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public DeliverTransaction getDeliverTransaction() {
        return deliverTransaction;
    }

    public void setDeliverTransaction(DeliverTransaction deliverTransaction) {
        this.deliverTransaction = deliverTransaction;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public BigInteger getNonce() {
        return this.nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }


    public static class Result {

        private String rawLog;

        private List<TransactionLog> logs;

        private List<TransactionEvent> events;

        public Result(){

        }

        public String getRawLog() {
            return rawLog;
        }

        public void setRawLog(String rawLog) {
            this.rawLog = rawLog;
        }

        public List<TransactionLog> getLogs() {
            return logs;
        }

        public void setLogs(List<TransactionLog> logs) {
            this.logs = logs;
        }

        public List<TransactionEvent> getEvents() {
            return events;
        }

        public void setEvents(List<TransactionEvent> events) {
            this.events = events;
        }


    }
}
