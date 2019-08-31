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
        String signedTransaction = this.sign(request);
        return this.getProvider().sendTransaction(signedTransaction, false);
    }

    @Override
    public String sign(TransactionRequest request) {
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

        TransactionPayload transactionPayload = new TransactionPayload(request.getAccountNumber(),
                request.getChainId(), request.getValue().getFee(), request.getValue().getMemo(), request.getValue().getMsg(), request.getNonce());

        try {
            String payload = this.objectMapper.writeValueAsString(transactionPayload);
            byte[] hash = payload.getBytes(StandardCharsets.UTF_8);
            Sign.SignatureData signature = Sign.signMessage(hash, this.signingKey.getKeyPair(), true);
            return serialize(request, signature, this.signingKey.getCompressedPublicKey());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("unable to serialize transaction");
        }
    }

    protected  String serialize(TransactionRequest unsignedTransaction, Sign.SignatureData signature, String publicKey) {
        if(signature==null){
            try {
                return Base64s.encode(this.objectMapper.writeValueAsString(unsignedTransaction).getBytes(StandardCharsets.UTF_8));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("unable to encode Base64 transaction");
            }
        }

        if(unsignedTransaction.getValue()==null) {
            throw new IllegalArgumentException("Invalid unsigned transaction");
        }

        if(unsignedTransaction.getValue().getSignatures()==null) {
            unsignedTransaction.getValue().setSignatures(new ArrayList<>());
        }
        PublicKey pubKey = new PublicKey("tendermint/PubKeySecp256k1", Base64s.encode(Numeric.hexStringToByteArray(publicKey)));
        unsignedTransaction.getValue().getSignatures().add(new Signature(pubKey, Base64s.encode(Sign.joinSignature(signature))));

        try {
            String json = objectMapper.writeValueAsString(unsignedTransaction);
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
}
