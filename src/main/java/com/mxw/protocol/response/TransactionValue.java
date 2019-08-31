package com.mxw.protocol.response;

import java.util.ArrayList;
import java.util.List;

public class TransactionValue  {

    private List<Signature> signatures;

    private TransactionFee fee;

    private String memo;

    private List<TransactionMessage> msg;

    public TransactionValue() {
        this.msg = new ArrayList<>();
        this.signatures = new ArrayList<>();
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

    public List<TransactionMessage> getMsg() {
        return msg;
    }

    public void setMsg(List<TransactionMessage> msg) {
        this.msg = msg;
    }

    public List<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Signature> signatures) {
        this.signatures = signatures;
    }

}
