package com.mxw.protocol.request.messages.builder;

import com.mxw.protocol.response.TransactionValue;

public interface TransactionValueBuilder {

    String getRoute();

    String getTransactionType();

    TransactionValue build();

}
