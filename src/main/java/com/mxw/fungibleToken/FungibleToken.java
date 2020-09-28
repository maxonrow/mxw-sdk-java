package com.mxw.fungibleToken;


import com.mxw.Signer;
import com.mxw.Wallet;
import com.mxw.protocol.common.Bundle;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.fungibleToken.*;
import com.mxw.protocol.response.Signature;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.protocol.response.fungibleToken.*;
import com.mxw.providers.Provider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class FungibleToken {
    private final Signer signer;
    private final Provider provider;

    private final String symbol;
    private FungibleTokenState state;

    public FungibleToken(String symbol, Signer signer) throws Exception {
        if (symbol == null || symbol.length() <= 0)
            throw new Exception("symbol is required");
        if (signer == null)
            throw new Exception("invalid signer");
        if (signer.getProvider() == null)
            throw new Exception("missing provider");
        this.symbol = symbol;
        this.signer = signer;
        this.provider = signer.getProvider();
    }

    public FungibleTokenState state() {
        return state;
    }

    public boolean isApproved() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        return (FungibleTokenEnum.FungibleTokenStateFlags.APPROVED.getValue()
                & state.getFlags()) == FungibleTokenEnum.FungibleTokenStateFlags.APPROVED.getValue();
    }

    public boolean isFrozen() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        return (FungibleTokenEnum.FungibleTokenStateFlags.FROZEN.getValue()
                & state.getFlags()) == FungibleTokenEnum.FungibleTokenStateFlags.FROZEN.getValue();
    }

    public boolean isMintable() throws Exception {
        isUsable();

        if (state == null)
            throw new Exception("not initialized state");
        return (FungibleTokenEnum.FungibleTokenStateFlags.MINT.getValue()
                & state.getFlags()) == FungibleTokenEnum.FungibleTokenStateFlags.MINT.getValue();
    }

    public boolean isBurnable() throws Exception {
        isUsable();

        if (state == null)
            throw new Exception("not initialized state");
        return (FungibleTokenEnum.FungibleTokenStateFlags.BURN.getValue()
                & state.getFlags()) == FungibleTokenEnum.FungibleTokenStateFlags.BURN.getValue();
    }

    public boolean isUsable() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        if (!isApproved())
            throw new Exception("required approval");
        if (isFrozen())
            throw new Exception("frozen");
        return true;
    }

    public FungibleTokenState refresh(Bundle overrides) throws Exception {
        if (this.symbol == null)
            throw new Exception("not initialized symbol");
        if (overrides == null) overrides = new Bundle();
        return this.state = getState(null, overrides.put("queryOnly", true));
    }

    public FungibleTokenState getState(BlockTag blockTag, Bundle overrides) throws Exception {
        if (symbol == null)
            throw new Exception("not initialized symbol");
        FungibleTokenState result = provider.getFungibleTokenState(symbol, blockTag);
        if (result == null)
            throw new Exception("token state is not available");
        if ((FungibleTokenEnum.FungibleTokenStateFlags.FUNGIBLE.getValue()
                & result.getFlags()) != FungibleTokenEnum.FungibleTokenStateFlags.FUNGIBLE.getValue())
            throw new Exception("class type mismatch");
        if (!this.symbol.equals(result.getSymbol()))
            throw new Exception("token symbol mismatch");
        if (overrides != null && overrides.contain("queryOnly"))
            this.state = result;

        return result;
    }

    public static FungibleToken fromSymbol(String symbol, Signer signer, Bundle overrides)
            throws Exception {
        FungibleToken token = new FungibleToken(symbol, signer);
        token.refresh(overrides);
        return token;
    }

    public static TransactionRequest create(FungibleTokenCreate item, Signer signer, Bundle overrides)
            throws Exception {
        if (overrides == null) overrides = new Bundle();

        TransactionRequest request = getCreateTransactionRequest(item, signer, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return request;

    }

    private static TransactionRequest getCreateTransactionRequest(FungibleTokenCreate item, Signer signer, Bundle overrides)
            throws Exception {
        if (signer == null)
            throw new Exception("create fungible token transaction require signer");
        if (item == null || item.getName() == null || item.getSymbol() == null ||
                item.getFee() == null || item.getFee().getTo() == null ||
                item.getFee().getValue() == null)
            throw new Exception("create fungible token ownership properties missing");
        if (new BigInteger(item.getFee().getValue()).signum() != 1)
            throw new Exception("create non fungible token transaction require non-negative application fee");

        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("create fungible token ownership require signer address");
        item.setOwner(address);
        TransactionRequest request = signer.getProvider().getTransactionRequest("token", "token-createNonFungibleToken",
                new FungibleTokenCreateBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    public TransactionRequest burn(FungibleTokenBurn item, Bundle overrides) throws Exception {
        if (item == null)
            throw new Exception("burn item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getBurnTransactionRequest(item, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return request;
    }

    private TransactionRequest getBurnTransactionRequest(FungibleTokenBurn item, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("burn fungible token item require signer");
        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("burn fungible token item require signer address");

        item.setFrom(address);
        TransactionRequest request = signer.getProvider().getTransactionRequest("token", "burnFungibleToken",
                new FungibleTokenBurnBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    public TransactionRequest transfer(FungibleTokenTransfer item, String toAddressOrName, Bundle overrides) throws Exception {
        if (item == null)
            throw new Exception("transfer item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getTransferTransactionRequest(item, toAddressOrName, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return request;
    }

    private TransactionRequest getTransferTransactionRequest(FungibleTokenTransfer item, String toAddressOrName, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("transfer fungible token item require signer");
        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("transfer fungible token item require signer address");
        String toAddress = signer.getProvider().resolveName(toAddressOrName);
        if (toAddress == null)
            throw new Exception("transfer fungible token require to address");
        item.setFrom(address);
        item.setTo(toAddress);

        TransactionRequest request = signer.getProvider().getTransactionRequest("token", "transferFungibleToken",
                new FungibleTokenTransferBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    public TransactionRequest mint(FungibleTokenMint item, String toAddressOrName, Bundle overrides)
            throws Exception {
        if (item == null)
            throw new Exception("mint item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getMintTransactionRequest(item, toAddressOrName, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return request;
    }

    private TransactionRequest getMintTransactionRequest(FungibleTokenMint item, String toAddressOrName, Bundle overrides)
            throws Exception {
        if (!isMintable())
            throw new Exception("fungible token can not be mint");
        if (signer == null)
            throw new Exception("mint fungible token require signer");
        String signerAddress = signer.getProvider().resolveName(signer.getAddress());
        if (signerAddress == null)
            throw new Exception("mint fungible token require signer address");
        String toAddress = signer.getProvider().resolveName(toAddressOrName);
        if (toAddress == null)
            throw new Exception("mint fungible token require to address");

        item.setOwner(signerAddress);
        item.setTo(toAddress);

        TransactionRequest request = signer.getProvider().getTransactionRequest("token", "mintFungibleToken",
                new FungibleTokenMintBuilder(item, overrides.get("memo", "")));

        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    public static FungibleTokenStatusPayload createFungibleTokenStatusPayload(String symbol, FungibleTokenEnum.FungibleTokenStatusActions status, Wallet signer, Bundle overrides)
            throws Exception {
        if (overrides == null) overrides = new Bundle();
        if (signer == null)
            throw new Exception("set fungible token status transaction require signer");

        FungibleTokenStatusPayload payload = new FungibleTokenStatusPayload();
        switch (status) {
            case APPROVE:
                if (overrides.contain("tokenFees")) {
                    if (overrides.get("tokenFees", null) == null)
                        throw new Exception("fungible token fees are missing");
                    if (overrides.get("tokenFees") instanceof ArrayList<?>)
                        payload.token.tokenFees = overrides.get("tokenFees", null);
                    if (overrides.get("tokenFees") instanceof FungibleTokenStatusFee[]) {
                        payload.token.tokenFees = new ArrayList<>();
                        Collections.addAll(payload.token.tokenFees, (FungibleTokenStatusFee[]) overrides.get("tokenFees"));
                    }
                }
                break;
            case APPROVE_TRANSFER_TOKEN_OWNERSHIP:
            case REJECT_TRANSFER_TOKEN_OWNERSHIP:
            case REJECT:
            case FREEZE:
            case UNFREEZE:
                break;
            default:
                throw new Exception("invalid fungible token status");
        }

        payload.token.from = signer.getAddress();
        if (overrides.contain("nonce"))
            payload.token.nonce = overrides.get("nonce").toString();
        if (payload.token.nonce == null)
            payload.token.nonce = signer.getProvider().getTransactionCount(signer.getAddress()).toString();
        payload.token.status = status.toString();
        payload.token.symbol = symbol;

        payload.token.burnable = overrides.get("burnable", false);

        Signature signature = signer.getSignature(payload.token);
        payload.pub_key = signature.getPublicKey();
        payload.signature = signature.getSignature();

        return payload;
    }

    public static FungibleTokenStatusTransaction signFungibleTokenStatusTransaction(FungibleTokenStatusPayload payload, Wallet signer, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("set fungible token status transaction require signer");
        if (payload == null)
            throw new Exception("payload item missing");
        FungibleTokenStatusTransaction transaction = new FungibleTokenStatusTransaction(payload);
        transaction.signatures.add(signer.getSignature(payload));

        return transaction;
    }

    public TransactionRequest sendFungibleTokenStatusTransaction(FungibleTokenStatusTransaction transaction, Bundle overrides) throws Exception {
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getFungibleTokenStatusTransactionRequest(transaction, signer, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);

        return request;
    }

    private TransactionRequest getFungibleTokenStatusTransactionRequest(FungibleTokenStatusTransaction transaction, Signer signer, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("set fungible token status transaction require signer");
        if (transaction == null || transaction.payload == null || transaction.signatures == null)
            throw new Exception("transaction item missing");

        transaction.owner = signer.getAddress();

        TransactionRequest request = signer.getProvider()
                .getTransactionRequest("token", "setFungibleTokenStatus",
                        new FungibleTokenStatusBuilder(transaction, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));

        return request;
    }

}
