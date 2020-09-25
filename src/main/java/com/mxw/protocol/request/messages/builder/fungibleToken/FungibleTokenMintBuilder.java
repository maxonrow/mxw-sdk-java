package com.mxw.protocol.request.messages.builder.fungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.fungibleToken.FungibleTokenMint;
import com.mxw.protocol.response.nonFungibleToken.NFTokenMint;

public class FungibleTokenMintBuilder implements TransactionValueBuilder {
    private final FungibleTokenMint fungibleTokenMint;
    private final String memo;

    public FungibleTokenMintBuilder(FungibleTokenMint fungibleTokenMint, String memo) {
        this.fungibleTokenMint = fungibleTokenMint;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "mintFungibleToken";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("token/mintFungibleToken");
        //noinspection unchecked
        message.setValue(this.fungibleTokenMint);
        value.getMsg().add(message);

        return value;
    }
}
