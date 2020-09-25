package com.mxw.protocol.request.messages.builder.fungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.fungibleToken.FungibleTokenStatusTransaction;
import com.mxw.protocol.response.nonFungibleToken.NFTokenStatusTransaction;

public class FungibleTokenStatusBuilder implements TransactionValueBuilder {
    private final FungibleTokenStatusTransaction fungibleTokenStatusTransaction;
    private final String memo;

    public FungibleTokenStatusBuilder(FungibleTokenStatusTransaction fungibleTokenStatusTransaction, String memo) {
        this.fungibleTokenStatusTransaction = fungibleTokenStatusTransaction;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "setFungibleTokenStatus";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("token/setFungibleTokenStatus");
        //noinspection unchecked
        message.setValue(this.fungibleTokenStatusTransaction);
        value.getMsg().add(message);

        return value;
    }
}
