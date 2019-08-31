package com.mxw.protocol.response;


import java.math.BigInteger;
import java.util.List;


public class TransactionFee {

    public TransactionFee() {

    }

    private List<Coin> amount;

    private BigInteger gas;

    public List<Coin> getAmount() {
        return amount;
    }

    public void setAmount(List<Coin> amount) {
        this.amount = amount;
    }

    public BigInteger getGas() {
        return gas;
    }

    public void setGas(BigInteger gas) {
        this.gas = gas;
    }
}
