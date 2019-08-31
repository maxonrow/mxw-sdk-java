package com.mxw.protocol.response;

import com.mxw.utils.Numeric;

public class TokenAccountState {

    public TokenAccountState(){

    }

    private String owner;

    private Boolean frozen;

    private Numeric balance;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public Numeric getBalance() {
        return balance;
    }

    public void setBalance(Numeric balance) {
        this.balance = balance;
    }
}
