package com.mxw.protocol.request.messages.builder.fungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.fungibleToken.FungibleTokenBurn;
import com.mxw.protocol.response.nonFungibleToken.NFTokenBurn;

public class FungibleTokenBurnBuilder implements TransactionValueBuilder {
    private FungibleTokenBurn fungibleTokenBurn;
    private String memo;

    public FungibleTokenBurnBuilder(FungibleTokenBurn fungibleTokenBurn, String memo) {
        this.fungibleTokenBurn = fungibleTokenBurn;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "token-burnFungibleToken";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("token/burnFungibleToken");
        //noinspection unchecked
        message.setValue(this.fungibleTokenBurn);
        value.getMsg().add(message);

        return value;
    }
}
