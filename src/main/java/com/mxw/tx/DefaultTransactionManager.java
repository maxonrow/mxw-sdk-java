package com.mxw.tx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.crypto.Sign;
import com.mxw.crypto.SigningKey;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.request.BlockTagName;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.PublicKey;
import com.mxw.protocol.response.Signature;
import com.mxw.protocol.response.TransactionPayload;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.Provider;
import com.mxw.utils.Base64s;
import com.mxw.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.mxw.utils.Assertions.verifyPrecondition;

public class DefaultTransactionManager extends TransactionManager {

    protected ObjectMapper objectMapper;
    private SigningKey signingKey;

    private BigInteger accountNumber;


    public DefaultTransactionManager(Provider provider, SigningKey signingKey) {
        super(provider, signingKey.getAddress());
        this.objectMapper = ObjectMapperFactory.getObjectMapper();
        this.signingKey = signingKey;
    }

    @Override
    public TransactionResponse sendTransaction(TransactionValueBuilder builder) {
        TransactionRequest request = this.createTransaction(builder);
        this.signRequest(request);
        return this.sendTransaction(request);
    }

    @Override
    public TransactionResponse sendTransaction(TransactionRequest request) {
        return this.getProvider().sendTransaction(serialize(request), false);
    }

    @Override
    public Signature sign(Object payload) {
        String serialized = null;
        try {
            serialized = this.objectMapper.writeValueAsString(payload);
            byte[] hash = serialized.getBytes(StandardCharsets.UTF_8);

            Sign.SignatureData signature = Sign.signMessage(hash, signingKey.getKeyPair(), true);
            String encodedPubKey = Base64s.encode(Numeric.hexStringToByteArray(signingKey.getCompressedPublicKey()));
            PublicKey pubKey = new PublicKey("tendermint/PubKeySecp256k1", encodedPubKey);

            return new Signature(pubKey, Base64s.encode(Sign.joinSignature(signature)));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("unable to serialize transaction");
        }
    }

    @Override
    public TransactionRequest signRequest(TransactionRequest request) {
        if(request.getNonce() == null){
            request.setNonce(this.getNonce());
        }

        if(request.getAccountNumber() == null) {
            if(accountNumber==null)
                accountNumber = this.getProvider().getAccountNumber(this.getFromAddress(), null);
            request.setAccountNumber(accountNumber);
        }

        verifyPrecondition(request.getNonce() != null && request.getAccountNumber() != null &&
                request.getValue() != null && request.getValue().getMsg() != null, "missing transaction field");


        if(request.getValue().getFee()==null) {
            request.getValue().setFee(this.getProvider().getTransactionFee(null, null, request));
        }
        request.setSequence(request.getNonce());

        TransactionPayload transactionPayload = createPayload(request);

        addSignature(request, this.sign(transactionPayload));

        return request;
    }

    @Override
    public String signAndSerialize(TransactionRequest request) {
        return serialize((signRequest(request)));
    }

    private void addSignature(TransactionRequest request, Signature signature) {
        if(request.getValue()==null) {
            throw new IllegalArgumentException("Invalid unsigned transaction");
        }

        if(request.getValue().getSignatures()==null) {
            request.getValue().setSignatures(new ArrayList<>());
        }


        String encodedPubKey = Base64s.encode(Numeric.hexStringToByteArray(this.signingKey.getCompressedPublicKey()));

        for (Signature sign : request.getValue().getSignatures()) {
            if(sign.getPublicKey().getValue().equalsIgnoreCase(encodedPubKey)){
                return;
            }
        }

        request.getValue().getSignatures().add(signature);

    }

    protected  String serialize(TransactionRequest request) {

        if(request.getValue()==null) {
            throw new IllegalArgumentException("Invalid transaction");
        }

        if(request.getValue().getSignatures()==null || request.getValue().getSignatures().size() == 0){
            try {
                return Base64s.encode(this.objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("unable to encode Base64 transaction");
            }
        }

        try {
            String json = objectMapper.writeValueAsString(request);
            String encoded = Base64s.encode(json.getBytes(StandardCharsets.UTF_8));
            if(logger.isDebugEnabled()){
                logger.debug("serialize transaction - " + json);
                logger.debug("encoded signed transaction - " + encoded);
            }
            return encoded;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("unable to encode base64 tranasaction");
        }
    }

    protected BigInteger getNonce() {
        return this.getProvider().getTransactionCount(this.getFromAddress(), BlockTagName.PENDING);
    }

    private TransactionPayload createPayload(TransactionRequest request) {
        return  new TransactionPayload(request.getAccountNumber(),
                request.getChainId(), request.getValue().getFee(), request.getValue().getMemo(), request.getValue().getMsg(), request.getNonce());
    }
}
