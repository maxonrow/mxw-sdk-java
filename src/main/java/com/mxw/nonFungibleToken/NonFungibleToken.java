package com.mxw.nonFungibleToken;


import com.mxw.Signer;
import com.mxw.Wallet;
import com.mxw.protocol.common.Bundle;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.nonFungibleToken.*;
import com.mxw.protocol.response.*;
import com.mxw.protocol.response.nonFungibleToken.*;
import com.mxw.providers.Provider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class NonFungibleToken {
    private final Signer signer;
    private final Provider provider;

    private final String symbol;
    private NFTokenState state;

    public NonFungibleToken(String symbol, Signer signer) throws Exception {
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

    public NFTokenState state() {
        return state;
    }

    /**
     * Get non fungible token approval status.
     *
     * @throws Exception if the configuration is invalid.
     */
    public boolean isApproved() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        return (NonFungibleTokenEnum.NFTokenStateFlags.APPROVED.getValue()
                & state.getFlags()) == NonFungibleTokenEnum.NFTokenStateFlags.APPROVED.getValue();
    }

    /**
     * Get non fungible token freeze status
     *
     * @throws Exception if the configuration is invalid.
     */
    public boolean isFrozen() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        return (NonFungibleTokenEnum.NFTokenStateFlags.FROZEN.getValue()
                & state.getFlags()) == NonFungibleTokenEnum.NFTokenStateFlags.FROZEN.getValue();
    }

    /**
     * Get non fungible token usable status
     *
     * @throws Exception if the configuration is invalid.
     */
    public boolean isUsable() throws Exception {
        if (state == null)
            throw new Exception("not initialized state");
        if (!isApproved())
            throw new Exception("required approval");
        if (isFrozen())
            throw new Exception("frozen");
        return true;
    }

    /**
     * Refresh non fungible token
     *
     * @throws Exception if the configuration is invalid.
     */
    public NFTokenState refresh(Bundle overrides) throws Exception {
        if (this.symbol == null)
            throw new Exception("not initialized symbol");
        if (overrides == null) overrides = new Bundle();
        return this.state = getState(null, overrides.put("queryOnly", true));
    }

    /**
     * Get non fungible token state
     *
     * @throws Exception if the configuration is invalid.
     */
    public NFTokenState getState(BlockTag blockTag, Bundle overrides) throws Exception {
        if (symbol == null)
            throw new Exception("not initialized symbol");
        NFTokenState result = provider.getNFTokenState(symbol, blockTag);
        if (result == null)
            throw new Exception("token state is not available");
        if ((NonFungibleTokenEnum.NFTokenStateFlags.NON_FUNGIBLE.getValue()
                & result.getFlags()) != NonFungibleTokenEnum.NFTokenStateFlags.NON_FUNGIBLE.getValue())
            throw new Exception("class type mismatch");
        if (!this.symbol.equals(result.getSymbol()))
            throw new Exception("token symbol mismatch");
        if (overrides != null && overrides.contain("queryOnly"))
            this.state = result;

        return result;
    }

    /**
     * Transfer non fungible token ownership.
     *
     * @param addressOrName transfer to address
     * @param overrides     extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse transferOwnership(String addressOrName, Bundle overrides) throws Exception {
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getTransferOwnershipTransactionRequest(addressOrName, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getTransferOwnershipTransactionRequest(String addressOrName, Bundle overrides)
            throws Exception {
        if (signer == null)
            throw new Exception("invalid signer");
        if (signer.getAddress() == null)
            throw new Exception("transfer non fungible token ownership require signer address");
        String toAddress = provider.resolveName(addressOrName);
        if (toAddress == null || toAddress.length() <= 0)
            throw new Exception("invalid to address");

        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "transferNonFungibleTokenOwnership",
                new NFTokenTransferOwnershipBuilder(new NFTokenTransferOwnership(symbol, signer.getAddress(), toAddress),
                        overrides.get("memo", "")));

        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));

        return request;
    }

    /**
     * Accept non fungible token transfer ownership.
     *
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse acceptOwnership(Bundle overrides) throws Exception {
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getAcceptOwnershipTransactionRequest(overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getAcceptOwnershipTransactionRequest(Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("invalid signer");

        isUsable();
        String signerAddress = signer.getAddress();
        if (signerAddress == null)
            throw new Exception("accept fungible token ownership require signer address");

        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "acceptNonFungibleTokenOwnership",
                new NFTokenAcceptOwnershipBuilder(new NFTokenAcceptOwnership(this.symbol, signerAddress),
                        overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));

        return request;
    }

    /**
     * Create non fungible token object from symbol and signer.
     *
     * @param symbol    non fungible token symbol
     * @param signer    owner of the non fungible token
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public static NonFungibleToken fromSymbol(String symbol, Signer signer, Bundle overrides)
            throws Exception {
        NonFungibleToken token = new NonFungibleToken(symbol, signer);
        token.refresh(overrides);
        return token;
    }

    /**
     * Request create new non fungible token.
     *
     * @param item      new non fungible token configuration
     * @param signer    owner of the non fungible token
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public static TransactionResponse create(NFTokenCreate item, Signer signer, Bundle overrides)
            throws Exception {
        if (overrides == null) overrides = new Bundle();

        TransactionRequest request = getCreateTransactionRequest(item, signer, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);

        return signer.sendTransaction(request);
    }

    private static TransactionRequest getCreateTransactionRequest(NFTokenCreate item, Signer signer, Bundle overrides)
            throws Exception {
        if (signer == null)
            throw new Exception("create non fungible token transaction require signer");
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
                new NFTokenCreateBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    /**
     * Request mint non fungible token.
     *
     * @param item      mint non fungible token configuration
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse mint(NFTokenMint item, String toAddressOrName, Bundle overrides)
            throws Exception {
        if (item == null)
            throw new Exception("mint item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getMintTransactionRequest(item, toAddressOrName, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getMintTransactionRequest(NFTokenMint item, String toAddressOrName, Bundle overrides)
            throws Exception {
        if (signer == null)
            throw new Exception("mint non fungible token require signer");
        String signerAddress = signer.getProvider().resolveName(signer.getAddress());
        if (signerAddress == null)
            throw new Exception("mint fungible token require signer address");
        String toAddress = signer.getProvider().resolveName(toAddressOrName);
        if (toAddress == null)
            throw new Exception("mint fungible token require to address");

        item.setOwner(signerAddress);
        item.setTo(toAddress);

        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "mintNonFungibleItem",
                new NFTokenMintBuilder(item, overrides.get("memo", "")));

        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    /**
     * Request burn non fungible token item.
     *
     * @param item      new non fungible token configuration
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse burn(NFTokenBurn item, Bundle overrides) throws Exception {
        if (item == null)
            throw new Exception("burn item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getBurnTransactionRequest(item, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getBurnTransactionRequest(NFTokenBurn item, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("burn non fungible token item require signer");
        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("burn fungible token item require signer address");

        item.setFrom(address);
        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "burnNonFungibleItem",
                new NFTokenBurnBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    /**
     * Request non fungible token item transfer.
     *
     * @param item            non fungible token item configuration
     * @param toAddressOrName transfer to address
     * @param overrides       extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse transfer(NFTokenTransferItem item, String toAddressOrName, Bundle overrides) throws Exception {
        if (item == null)
            throw new Exception("transfer item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getTransferItemTransactionRequest(item, toAddressOrName, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getTransferItemTransactionRequest(NFTokenTransferItem item, String toAddressOrName, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("transfer non fungible token item require signer");
        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("transfer fungible token item require signer address");
        String toAddress = signer.getProvider().resolveName(toAddressOrName);
        if (toAddress == null)
            throw new Exception("mint fungible token require to address");
        item.setFrom(address);
        item.setTo(toAddress);

        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "transferNonFungibleItem",
                new NFTokenTransferItemBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    /**
     * Request non fungible token endorse.
     *
     * @param item      non fungible token item configuration
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse endorse(NFTokenEndorse item, Bundle overrides) throws Exception {
        if (item == null)
            throw new Exception("endorse item missing");
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getEndorseTransactionRequest(item, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);
        return signer.sendTransaction(request);
    }

    private TransactionRequest getEndorseTransactionRequest(NFTokenEndorse item, Bundle overrides)
            throws Exception {
        if (signer == null)
            throw new Exception("endorse non fungible token item require signer");
        String address = signer.getProvider().resolveName(signer.getAddress());
        if (address == null)
            throw new Exception("endorse fungible token item require signer address");
        item.setFrom(address);
        item.setMetadata(item.getMetadata() != null ? item.getMetadata() : "");

        TransactionRequest request = signer.getProvider().getTransactionRequest("nonFungible", "endorsement",
                new NFTokenEndorseBuilder(item, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));
        return request;
    }

    public NFTokenStatusFee checkNonFungibleTokenFee(NFTokenStatusFee data) throws Exception {
        if (data == null || data.action == null || data.action.length() <= 0)
            throw new Exception("invalid non fungible token fee");
        return data;
    }

    /**
     * Create update non fungible token item status payload.
     *
     * @param symbol    non fungible token symbol
     * @param status    status update to
     * @param signer    provider signer
     * @param overrides extra param {@link ArrayList< NFTokenStatusFee > tokenFees},
     *                  {@link ArrayList<String> endorserList}, {@link String mintLimit},
     *                  {@link String transferLimit}, {@link Boolean burnable},
     *                  {@link Boolean transferable}, {@link Boolean modifiable},
     *                  {@link Boolean pub}
     * @throws Exception if the configuration is invalid.
     */
    public static NFTokenStatusPayload createNFTokenStatusPayload(String symbol, NonFungibleTokenEnum.NonFungibleTokenStatusActions status, Wallet signer, Bundle overrides)
            throws Exception {
        if (overrides == null) overrides = new Bundle();
        if (signer == null)
            throw new Exception("set non fungible token status transaction require signer");

        NFTokenStatusPayload payload = new NFTokenStatusPayload();
        switch (status) {
            case APPROVE:
                try {
                    Integer.parseInt(overrides.get("mintLimit", "0"));
                } catch (Exception e) {
                    throw new Exception("set non fungible token status transaction invalid mintLimit");
                }
                try {
                    Integer.parseInt(overrides.get("transferLimit", "0"));
                } catch (Exception e) {
                    throw new Exception("set non fungible token status transaction invalid transferLimit");
                }

                payload.token.endorserListLimit = overrides.get("endorserListLimit", "0");
                Integer.parseUnsignedInt(payload.token.endorserListLimit);
                payload.token.mintLimit = overrides.get("mintLimit", "0");
                Integer.parseUnsignedInt(payload.token.mintLimit);
                payload.token.transferLimit = overrides.get("transferLimit", "0");
                Integer.parseUnsignedInt(payload.token.transferLimit);

                if (overrides.contain("tokenFees")) {
                    if (overrides.get("tokenFees", null) == null)
                        throw new Exception("non fungible token fees are missing");
                    if (overrides.get("tokenFees") instanceof ArrayList<?>)
                        payload.token.tokenFees = overrides.get("tokenFees", null);
                    if (overrides.get("tokenFees") instanceof NFTokenStatusFee[]) {
                        payload.token.tokenFees = new ArrayList<>();
                        Collections.addAll(payload.token.tokenFees, (NFTokenStatusFee[]) overrides.get("tokenFees"));
                    }
                }
                if (overrides.contain("endorserList")) {
                    if (overrides.get("endorserList") instanceof ArrayList<?>)
                        payload.token.endorserList = overrides.get("endorserList", null);
                    if (overrides.get("endorserList") instanceof String[]) {
                        payload.token.endorserList = new ArrayList<>();
                        Collections.addAll(payload.token.endorserList, (String[]) overrides.get("endorserList"));
                    }
                }
                break;
            case APPROVE_TRANSFER_TOKEN_OWNERSHIP:
            case REJECT_TRANSFER_TOKEN_OWNERSHIP:
            case REJECT:
            case FREEZE:
            case UNFREEZE:
                payload.token.endorserListLimit = "0";
                payload.token.mintLimit = "0";
                payload.token.transferLimit = "0";
                payload.token.endorserList = null;
                break;
            default:
                throw new Exception("invalid non fungible token status");
        }

        payload.token.from = signer.getAddress();
        if (overrides.contain("nonce"))
            payload.token.nonce = overrides.get("nonce").toString();
        if (payload.token.nonce == null)
            payload.token.nonce = signer.getProvider().getTransactionCount(signer.getAddress()).toString();
        payload.token.status = status.toString();
        payload.token.symbol = symbol;

        payload.token.burnable = overrides.get("burnable", false);
        payload.token.transferable = overrides.get("transferable", false);
        payload.token.modifiable = overrides.get("modifiable", false);
        payload.token.pub = overrides.get("pub", false);

        Signature signature = signer.getSignature(payload.token);
        payload.pub_key = signature.getPublicKey();
        payload.signature = signature.getSignature();

        return payload;
    }

    /**
     * Sign non fungible payload status and get transaction request.
     *
     * @param payload   payload to be sign
     * @param signer    issuer signer
     * @param overrides extra param
     * @throws Exception if the configuration is invalid.
     */
    public static NFTokenStatusTransaction signNonFungibleTokenStatusTransaction(NFTokenStatusPayload payload, Wallet signer, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("set non fungible token status transaction require signer");
        if (payload == null)
            throw new Exception("payload item missing");
        NFTokenStatusTransaction transaction = new NFTokenStatusTransaction(payload);
        transaction.signatures.add(signer.getSignature(payload));

        return transaction;
    }

    /**
     * Perform non fungible status update.
     *
     * @param transaction transaction request
     * @param overrides   extra param
     * @throws Exception if the configuration is invalid.
     */
    public TransactionResponse sendNonFungibleTokenStatusTransaction(NFTokenStatusTransaction transaction, Bundle overrides) throws Exception {
        if (overrides == null) overrides = new Bundle();
        TransactionRequest request = getNonFungibleTokenStatusTransactionRequest(transaction, signer, overrides);
        request.setChainId(signer.getProvider().getNetwork().getChainId());
        signer.sign(request);

        return signer.sendTransaction(request);
    }

    private TransactionRequest getNonFungibleTokenStatusTransactionRequest(NFTokenStatusTransaction transaction, Signer signer, Bundle overrides) throws Exception {
        if (signer == null)
            throw new Exception("set non fungible token status transaction require signer");
        if (transaction == null || transaction.payload == null || transaction.signatures == null)
            throw new Exception("transaction item missing");

        transaction.owner = signer.getAddress();

        TransactionRequest request = signer.getProvider()
                .getTransactionRequest("nonFungible", "setNonFungibleTokenStatus",
                        new NFTokenStatusBuilder(transaction, overrides.get("memo", "")));
        request.getValue().setFee(signer.getProvider().getTransactionFee(null, null, request));

        return request;
    }

}
