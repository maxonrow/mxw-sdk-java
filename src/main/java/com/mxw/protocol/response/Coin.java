package com.mxw.protocol.response;

import java.math.BigInteger;

public class Coin {

    public Coin(){

    }

    public Coin(BigInteger amount, String denom){
        this.amount = amount;
        this.denom = denom;
    }

    private BigInteger amount;

    private String denom;

    public String getDenom() {
        return denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
