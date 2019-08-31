package com.mxw.protocol.response;

import java.math.BigInteger;
import java.util.List;

public class BlockTransaction {

    private String hash;

    private String rawLog;

    private List<TransactionLog> logs;

    private List<TransactionEvent> events;

    private BigInteger nonce;

    private Integer transactionIndex;

    private BigInteger gasUsed;

    public BlockTransaction(){

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public Integer getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(Integer transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getRawLog() {
        return rawLog;
    }

    public void setRawLog(String rawLog) {
        this.rawLog = rawLog;
    }
}
