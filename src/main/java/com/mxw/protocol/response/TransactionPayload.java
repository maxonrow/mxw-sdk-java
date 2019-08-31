package com.mxw.protocol.response;

import java.math.BigInteger;
import java.util.List;

public class TransactionPayload {

    private BigInteger accountNumber;
    private String chainId;
    private TransactionFee fee;
    private String memo;
    private List<TransactionMessage> msgs;
    private BigInteger sequence;

    public TransactionPayload() {

    }

    public TransactionPayload(BigInteger accountNumber, String chainId, TransactionFee fee,
                              String memo, List<TransactionMessage> msgs, BigInteger sequence){
        this.accountNumber = accountNumber;
        this.chainId = chainId;
        this.fee = fee;
        this.memo = memo;
        this.msgs = msgs;
        this.sequence = sequence;
    }

    public BigInteger getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(BigInteger accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public TransactionFee getFee() {
        return fee;
    }

    public void setFee(TransactionFee fee) {
        this.fee = fee;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<TransactionMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<TransactionMessage> msgs) {
        this.msgs = msgs;
    }

    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(BigInteger sequence) {
        this.sequence = sequence;
    }
}
