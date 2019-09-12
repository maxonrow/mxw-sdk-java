package com.mxw.protocol.request.messages.builder;

import com.mxw.protocol.response.kyc.KycWhitelist;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.response.TransactionValue;

public class KycWhiteListBuilder implements TransactionValueBuilder {

    private KycWhitelist kycWhitelist;

    public KycWhiteListBuilder(KycWhitelist kycWhitelist){
       this.kycWhitelist = kycWhitelist;
    }

    @Override
    public String getRoute() {
        return "kyc";
    }

    @Override
    public String getTransactionType() {
        return "kyc-whitelist";
    }

    @Override
    public TransactionValue build() {

        TransactionValue value = new TransactionValue();
        value.setMemo("");

        TransactionMessage message = new TransactionMessage<>();
        message.setType("kyc/whitelist");
        message.setValue(this.kycWhitelist);
        value.getMsg().add(message);

        return value;
    }
}
