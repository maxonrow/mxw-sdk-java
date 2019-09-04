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
        this.sign(request);
        return this.sendTransaction(request);
    }

    @Override
    public TransactionResponse sendTransaction(TransactionRequest request) {
        return this.getProvider().sendTransaction(serialize(request), false);
    }

    @Override
    public TransactionRequest sign(TransactionRequest request) {
        if(request.getNonce() == null){
            request.setNonce(this.getProvider().getTransactionCount(this.getFromAddress(), BlockTagName.PENDING));
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

        try {
            String payload = this.objectMapper.writeValueAsString(transactionPayload);
            byte[] hash = payload.getBytes(StandardCharsets.UTF_8);
            Sign.SignatureData signature = Sign.signMessage(hash, this.signingKey.getKeyPair(), true);
            addSignature(request, signature, this.signingKey.getCompressedPublicKey());
            return request;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("unable to serialize transaction");
        }
    }

    @Override
    public String signAndSerialize(TransactionRequest request) {
        return serialize((sign(request)));
    }

    public void addSignature(TransactionRequest request, Sign.SignatureData signature, String publicKey) {
        if(request.getValue()==null) {
            throw new IllegalArgumentException("Invalid unsigned transaction");
        }

        if(request.getValue().getSignatures()==null) {
            request.getValue().setSignatures(new ArrayList<>());
        }


        String encodedPubKey = Base64s.encode(Numeric.hexStringToByteArray(publicKey));

        for (Signature sign : request.getValue().getSignatures()) {
            if(sign.getPublicKey().getValue().equalsIgnoreCase(encodedPubKey)){
                return;
            }
        }

        // if public key not appear in signatures, add in
        PublicKey pubKey = new PublicKey("tendermint/PubKeySecp256k1", encodedPubKey);
        request.getValue().getSignatures().add(new Signature(pubKey, Base64s.encode(Sign.joinSignature(signature))));

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

    private TransactionPayload createPayload(TransactionRequest request) {
        return  new TransactionPayload(request.getAccountNumber(),
                request.getChainId(), request.getValue().getFee(), request.getValue().getMemo(), request.getValue().getMsg(), request.getNonce());
    }
}
