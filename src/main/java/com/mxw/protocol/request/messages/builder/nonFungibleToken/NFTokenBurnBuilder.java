package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenBurn;

public class NFTokenBurnBuilder implements TransactionValueBuilder {
    private NFTokenBurn nfTokenBurn;
    private String memo;

    public NFTokenBurnBuilder(NFTokenBurn nfTokenBurn, String memo) {
        this.nfTokenBurn = nfTokenBurn;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "burnNonFungibleItem";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/burnNonFungibleItem");
        //noinspection unchecked
        message.setValue(this.nfTokenBurn);
        value.getMsg().add(message);

        return value;
    }
}
