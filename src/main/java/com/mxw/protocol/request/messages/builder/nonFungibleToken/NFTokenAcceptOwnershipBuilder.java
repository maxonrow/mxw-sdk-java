package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenAcceptOwnership;

public class NFTokenAcceptOwnershipBuilder implements TransactionValueBuilder {
    private final NFTokenAcceptOwnership nfTokenAcceptOwnership;
    private final String memo;

    public NFTokenAcceptOwnershipBuilder(NFTokenAcceptOwnership nfTokenAcceptOwnership, String memo) {
        this.nfTokenAcceptOwnership = nfTokenAcceptOwnership;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "acceptNonFungibleTokenOwnership";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/acceptNonFungibleTokenOwnership");
        //noinspection unchecked
        message.setValue(this.nfTokenAcceptOwnership);
        value.getMsg().add(message);

        return value;
    }
}
