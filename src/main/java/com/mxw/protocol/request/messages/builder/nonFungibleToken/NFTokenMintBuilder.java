package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenMint;

public class NFTokenMintBuilder implements TransactionValueBuilder {
    private final NFTokenMint nfTokenMint;
    private final String memo;

    public NFTokenMintBuilder(NFTokenMint nfTokenMint, String memo) {
        this.nfTokenMint = nfTokenMint;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "mintNonFungibleItem";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/mintNonFungibleItem");
        //noinspection unchecked
        message.setValue(this.nfTokenMint);
        value.getMsg().add(message);

        return value;
    }
}
