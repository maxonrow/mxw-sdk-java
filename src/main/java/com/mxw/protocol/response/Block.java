package com.mxw.protocol.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxw.protocol.deserializer.BlockResultDeserializer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Block {

    @JsonProperty("height")
    private BigInteger blockNumber;

    private Results results;

    public Block(){

    }

    @JsonDeserialize(using= BlockResultDeserializer.class)
    public static class Results {

        @JsonProperty("deliver_tx")
        private List<BlockTransaction> transactions;

        public Results(){
            transactions = new ArrayList<>();
        }

        public List<BlockTransaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<BlockTransaction> transactions) {
            this.transactions = transactions;
            if(this.transactions==null){
                this.transactions = new ArrayList<>();
            }
        }
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }
}
