package com.mxw.protocol.request.messages.builder;

import com.mxw.protocol.request.messages.BankSend;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;

import java.math.BigInteger;

public class BankSendBuilder implements TransactionValueBuilder {

    private BankSend bankSend;
    private String memo;


    public BankSendBuilder(String from, String to, BigInteger value) {
        this(from, to, value,null, null);
    }

    public BankSendBuilder(String from, String to, BigInteger value, String memo) {
        this(from, to, value, memo, null);
    }

    public BankSendBuilder(String from, String to, BigInteger value, String memo, String denom) {
        this.bankSend = new BankSend(from, to, value, denom);
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "bank";
    }

    @Override
    public String getTransactionType() {
        return "bank-send";
    }

    @Override
    public TransactionValue build() {
        TransactionValue value = new TransactionValue();
        value.setMemo(this.memo);
        TransactionMessage<BankSend> message = new TransactionMessage<>();
        message.setType("mxw/msgSend");
        message.setValue(this.bankSend);
        value.getMsg().add(message);
        return value;
    }
}
