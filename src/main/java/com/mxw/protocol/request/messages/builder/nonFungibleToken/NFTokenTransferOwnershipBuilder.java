package com.mxw.protocol.request.messages.builder.nonFungibleToken;

import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;
import com.mxw.protocol.response.nonFungibleToken.NFTokenTransferOwnership;

public class NFTokenTransferOwnershipBuilder implements TransactionValueBuilder {
    private final NFTokenTransferOwnership nfTokenTransferOwnership;
    private final String memo;

    public NFTokenTransferOwnershipBuilder(NFTokenTransferOwnership nfTokenTransferOwnership, String memo) {
        this.nfTokenTransferOwnership = nfTokenTransferOwnership;
        this.memo = memo;
    }

    @Override
    public String getRoute() {
        return "nonFungible";
    }

    @Override
    public String getTransactionType() {
        return "transferNonFungibleTokenOwnership";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo(memo != null ? memo : "");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("nonFungible/transferNonFungibleTokenOwnership");
        //noinspection unchecked
        message.setValue(this.nfTokenTransferOwnership);
        value.getMsg().add(message);

        return value;
    }
}
