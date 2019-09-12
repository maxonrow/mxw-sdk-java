package com.mxw.protocol.request.messages.builder;

import com.mxw.protocol.response.kyc.KycRevoke;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;

public class KycRevokeBuilder implements TransactionValueBuilder {

    private KycRevoke kycRevoke;

    public KycRevokeBuilder(KycRevoke kycRevoke){
        this.kycRevoke = kycRevoke;
    }

    @Override
    public String getRoute() {
        return "kyc";
    }

    @Override
    public String getTransactionType() {
        return "kyc-revokeWhitelist";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo("");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("kyc/revokeWhitelist");
        message.setValue(this.kycRevoke);
        value.getMsg().add(message);

        return value;
    }
}
