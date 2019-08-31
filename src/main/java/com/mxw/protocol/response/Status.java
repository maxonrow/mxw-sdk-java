package com.mxw.protocol.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mxw.protocol.common.Response;
import com.mxw.utils.Numeric;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

public class Status {


    private NodeInfo nodeInfo;

    private SyncInfo syncInfo;

    private ValidatorInfo validatorInfo;


    public static class SyncInfo {
        private String latestBlockHash;
        private String latestAppHash;
        private String latestBlockHeight;

        private String latestBlockTime;
        private Boolean catchingUp;

        public SyncInfo(){

        }

        public String getLatestBlockHash() {
            return latestBlockHash;
        }

        public void setLatestBlockHash(String latestBlockHash) {
            this.latestBlockHash = latestBlockHash;
        }

        public String getLatestAppHash() {
            return latestAppHash;
        }

        public void setLatestAppHash(String latestAppHash) {
            this.latestAppHash = latestAppHash;
        }

        public BigInteger getLatestBlockHeight() {
            return new BigInteger(this.latestBlockHeight);
        }

        public void setLatestBlockHeight(String latestBlockHeight) {
            this.latestBlockHeight = latestBlockHeight;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
        public Date getLatestBlockTime() {
            return Date.from(Instant.parse(this.latestBlockTime));
        }

        public void setLatestBlockTime(String latestBlockTime) {
            this.latestBlockTime = latestBlockTime;
        }

        public Boolean getCatchingUp() {
            return catchingUp;
        }

        public void setCatchingUp(Boolean catchingUp) {
            this.catchingUp = catchingUp;
        }
    }


    public static class ValidatorInfo {
        private String address;
        private PublicKey pubKey;
        private BigInteger votingPower;

        public ValidatorInfo() {

        }

        public ValidatorInfo(String address, PublicKey pubKey, BigInteger votingPower){
            this.address = address;
            this.pubKey = pubKey;
            this.votingPower = votingPower;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public PublicKey getPubKey() {
            return pubKey;
        }

        public void setPubKey(PublicKey pubKey) {
            this.pubKey = pubKey;
        }

        public BigInteger getVotingPower() {
            return votingPower;
        }

        public void setVotingPower(BigInteger votingPower) {
            this.votingPower = votingPower;
        }
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public SyncInfo getSyncInfo() {
        return syncInfo;
    }

    public void setSyncInfo(SyncInfo syncInfo) {
        this.syncInfo = syncInfo;
    }

    public ValidatorInfo getValidatorInfo() {
        return validatorInfo;
    }

    public void setValidatorInfo(ValidatorInfo validatorInfo) {
        this.validatorInfo = validatorInfo;
    }
}
