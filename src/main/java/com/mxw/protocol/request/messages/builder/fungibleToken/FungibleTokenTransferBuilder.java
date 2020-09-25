package com.mxw.protocol.request.messages.builder.fungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.fungibleToken.FungibleTokenTransfer;
import com.mxw.protocol.response.nonFungibleToken.NFTokenTransferItem;

public class FungibleTokenTransferBuilder implements TransactionValueBuilder {
    private FungibleTokenTransfer fungibleTokenTransfer;
    private String memo;

    public FungibleTokenTransferBuilder(FungibleTokenTransfer fungibleTokenTransfer, String memo) {
        this.fungibleTokenTransfer = fungibleTokenTransfer;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "transferFungibleToken";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("token/transferFungibleToken");
        //noinspection unchecked
        message.setValue(this.fungibleTokenTransfer);
        value.getMsg().add(message);

        return value;
    }
}
