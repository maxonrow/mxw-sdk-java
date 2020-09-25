package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenCreate;

public class NFTokenCreateBuilder implements TransactionValueBuilder {
    private final NFTokenCreate nfTokenCreate;
    private final String memo;

    public NFTokenCreateBuilder(NFTokenCreate nfTokenCreate, String memo) {
        this.nfTokenCreate = nfTokenCreate;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "token-createNonFungibleToken";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/createNonFungibleToken");
        //noinspection unchecked
        message.setValue(this.nfTokenCreate);
        value.getMsg().add(message);

        return value;
    }
}
