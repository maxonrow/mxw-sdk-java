package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenTransferItem;

public class NFTokenTransferItemBuilder implements TransactionValueBuilder {
    private NFTokenTransferItem nfTokenTransferItem;
    private String memo;

    public NFTokenTransferItemBuilder(NFTokenTransferItem nfTokenTransferItem, String memo) {
        this.nfTokenTransferItem = nfTokenTransferItem;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "transferNonFungibleItem";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/transferNonFungibleItem");
        //noinspection unchecked
        message.setValue(this.nfTokenTransferItem);
        value.getMsg().add(message);

        return value;
    }
}
