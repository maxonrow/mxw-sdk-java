package com.mxw;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.providers.Provider;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.response.TransactionResponse;

public interface Signer {

    Provider getProvider();

    String getAddress();

    String getHexAddress();

    String getPublicKeyType();

    String getCompressedPublicKey();

    String signMessage(byte[] message, boolean needToHash);

    String sign(TransactionRequest request);

    TransactionResponse sendTransaction(TransactionValueBuilder builder);

    TransactionResponse sendTransaction(TransactionRequest request);
}
