package com.mxw.protocol.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxw.protocol.deserializer.PublicKeyDeserializer;

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

        @JsonDeserialize(using = PublicKeyDeserializer.class)
        private String publicKey;

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

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
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
