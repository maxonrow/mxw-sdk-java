package com.mxw.tx;

import com.mxw.crypto.SigningKey;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.providers.Provider;
import com.mxw.utils.Strings;

import java.io.IOException;
import java.math.BigInteger;

public class FastTransactionManager extends DefaultTransactionManager {

    private BigInteger nonce = BigInteger.valueOf(-1);


    public FastTransactionManager(Provider provider, SigningKey signingKey) {
        super(provider, signingKey);
    }

    @Override
    public TransactionRequest createTransaction(TransactionValueBuilder builder) {
        TransactionRequest request = this.getProvider().getTransactionRequest(builder.getRoute(), builder.getTransactionType(), builder);

        if (Strings.isEmpty(request.getChainId())) {
            request.setChainId(this.getProvider().getNetwork().getChainId());
        }

        request.getValue().setFee(this.getProvider().getTransactionFee(null, null, request));

        return request;
    }

    @Override
    public TransactionRequest signRequest(TransactionRequest request) {
        if(request.getNonce()==null || request.getNonce().signum() == -1) {
            request.setNonce(this.getNonce());
        }
        return super.signRequest(request);
    }

    protected synchronized BigInteger getNonce() {
        if(nonce.signum()== -1) {
            nonce = this.getProvider().getTransactionCount(this.getFromAddress());
        }else {
            nonce = nonce.add(BigInteger.ONE);
        }
        return nonce;
    }

    protected synchronized void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getCurrentNonce() {
        return nonce;
    }

    public synchronized void resetNonce() throws IOException {
        nonce = super.getNonce();
    }

}
