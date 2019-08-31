package com.mxw.protocol.request.messages;

import com.mxw.protocol.response.Coin;
import com.mxw.utils.Convert;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class BankSend {

    private List<Coin> amount;
    private String fromAddress;
    private String toAddress;

    public BankSend() {

    }

    public BankSend(String fromAddress, String toAddress,  BigInteger amount){
        this(fromAddress, toAddress, amount,Convert.Unit.CIN.toString());
    }
    public BankSend(String fromAddress, String toAddress,  BigInteger amount, String denom){
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        if(denom==null)
            denom = Convert.Unit.CIN.toString();
        this.amount = Collections.singletonList(new Coin(amount, denom));
    }

    public List<Coin> getAmount() {
        return amount;
    }

    public void setAmount(List<Coin> amount) {
        this.amount = amount;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}
