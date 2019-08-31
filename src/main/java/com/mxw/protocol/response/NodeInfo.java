package com.mxw.protocol.response;

import com.mxw.utils.Numeric;

import java.math.BigInteger;

public class NodeInfo {

    private ProtocolVersion protocolVersion;

    private String id;

    private String listenAddr;

    private String network;

    private String version;

    private String channels;

    private String moniker;

    private Other other;

    public static class ProtocolVersion {
        private String p2p;
        private String block;
        private String app;

        public ProtocolVersion() {

        }

        public ProtocolVersion(String p2p, String block,  String app){
            this.p2p = p2p;
            this.block = block;
            this.app = app;
        }

        public BigInteger getP2p() {
            return Numeric.decodeQuantity(this.p2p);
        }

        public void setP2p(String p2p) {
            this.p2p = p2p;
        }

        public BigInteger getBlock() {
            return Numeric.decodeQuantity(this.block);
        }

        public void setBlock(String block) {
            this.block = block;
        }

        public BigInteger getApp() {
            return Numeric.decodeQuantity(this.app);
        }

        public void setApp(String app) {
            this.app = app;
        }

    }

    public static class Other {
        private String txIndex;
        private String rpcAddress;

        public Other() {

        }

        public Other(String txIndex, String rpcAddress) {
            this.txIndex = txIndex;
            this.rpcAddress = rpcAddress;
        }

        public String getTxIndex() {
            return txIndex;
        }

        public void setTxIndex(String txIndex) {
            this.txIndex = txIndex;
        }

        public String getRpcAddress() {
            return rpcAddress;
        }

        public void setRpcAddress(String rpcAddress) {
            this.rpcAddress = rpcAddress;
        }
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListenAddr() {
        return listenAddr;
    }

    public void setListenAddr(String listenAddr) {
        this.listenAddr = listenAddr;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getMoniker() {
        return moniker;
    }

    public void setMoniker(String moniker) {
        this.moniker = moniker;
    }

    public Other getOther() {
        return other;
    }

    public void setOther(Other other) {
        this.other = other;
    }
}