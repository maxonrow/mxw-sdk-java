package com.mxw.tx;

import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.Signature;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.Provider;
import com.mxw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransactionManager {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Provider provider;
    private String fromAddress;


    public TransactionManager(Provider provider, String fromAddress) {
        this.provider = provider;
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * simplified send transaction with single signature
     *
     * @param builder
     * @return
     */
    public abstract TransactionResponse sendTransaction(TransactionValueBuilder builder);

    /**
     * capable to send transaction with multiple signatures
     *
     * @param request
     * @return
     */
    public abstract TransactionResponse sendTransaction(TransactionRequest request);

    public abstract Signature sign(Object payload);

    /**
     * Sign and append signature to transaction request
     *
     * @param request
     * @return
     */
    public abstract TransactionRequest signRequest(TransactionRequest request);

    public abstract String signAndSerialize(TransactionRequest request);

    public TransactionRequest createTransaction(TransactionValueBuilder builder) {
        TransactionRequest request = this.provider.getTransactionRequest(builder.getRoute(), builder.getTransactionType(), builder);

        if (request.getNonce() == null) {
            request.setNonce(provider.getTransactionCount(fromAddress));
        }

        if (Strings.isEmpty(request.getChainId())) {
            request.setChainId(provider.getNetwork().getChainId());
        }

        request.getValue().setFee(this.provider.getTransactionFee(null, null, request));

        return request;
    }

}
