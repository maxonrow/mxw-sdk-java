package com.mxw.protocol.response;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AccountState {

    public AccountState(){

    }

    private String type;

    private Value value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


    public static class Value {

        public Value(){

        }

        private String address;

        private List<Coin> coins = new ArrayList<Coin>();

        private PublicKey publicKey;

        private BigInteger accountNumber;

        private BigInteger sequence;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<Coin> getCoins() {
            return coins;
        }

        public void setCoins(List<Coin> coins) {
            this.coins = coins;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public BigInteger getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(BigInteger accountNumber) {
            this.accountNumber = accountNumber;
        }

        public BigInteger getSequence() {
            return sequence;
        }

        public void setSequence(BigInteger sequence) {
            this.sequence = sequence;
        }
    }

}
