package com.mxw.protocol.response;

import com.mxw.utils.Numeric;

import java.math.BigInteger;

public class TransactionLog {

    public TransactionLog(){

    }

    private Boolean success;

    private Info info;

    private String log;

    private Integer msgIndex;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Integer getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(Integer msgIndex) {
        this.msgIndex = msgIndex;
    }


    public static class Info {

        public Info(){

        }

        public BigInteger getNonce() {
            return nonce;
        }

        public void setNonce(BigInteger nonce) {
            this.nonce = nonce;
        }

        public String getHash() {
            return Numeric.prependHexPrefix(this.hash);
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private BigInteger nonce;

        private String hash;

        private String message;
    }

}
