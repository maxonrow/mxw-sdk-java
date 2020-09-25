package com.mxw.protocol.request.messages.builder.fungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.fungibleToken.FungibleTokenCreate;
import com.mxw.protocol.response.nonFungibleToken.NFTokenCreate;

public class FungibleTokenCreateBuilder implements TransactionValueBuilder {
    private final FungibleTokenCreate fTokenCreate;
    private final String memo;

    public FungibleTokenCreateBuilder(FungibleTokenCreate fTokenCreate, String memo) {
        this.fTokenCreate = fTokenCreate;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "token-createFungibleToken";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("token/createFungibleToken");
        //noinspection unchecked
        message.setValue(this.fTokenCreate);
        value.getMsg().add(message);

        return value;
    }
}
