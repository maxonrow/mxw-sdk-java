package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenStatusTransaction;

public class NFTokenStatusBuilder implements TransactionValueBuilder {
    private final NFTokenStatusTransaction nfTokenStatusTransaction;
    private final String memo;

    public NFTokenStatusBuilder(NFTokenStatusTransaction nfTokenStatusTransaction, String memo) {
        this.nfTokenStatusTransaction = nfTokenStatusTransaction;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "token";
    }

    @Override
    public String getTransactionType() {
        return "setNonFungibleTokenStatus";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/setNonFungibleTokenStatus");
        //noinspection unchecked
        message.setValue(this.nfTokenStatusTransaction);
        value.getMsg().add(message);

        return value;
    }
}
