package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenEndorse;

public class NFTokenEndorseBuilder implements TransactionValueBuilder {
    private final NFTokenEndorse nfTokenEndorse;
    private final String memo;

    public NFTokenEndorseBuilder(NFTokenEndorse nfTokenEndorse, String memo) {
        this.nfTokenEndorse = nfTokenEndorse;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "endorsement";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/endorsement");
        //noinspection unchecked
        message.setValue(this.nfTokenEndorse);
        value.getMsg().add(message);

        return value;
    }
}
