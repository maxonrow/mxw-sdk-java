package com.mxw;

import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;
import com.mxw.protocol.common.Bundle;
import com.mxw.nonFungibleToken.NonFungibleToken;
import com.mxw.nonFungibleToken.NonFungibleTokenEnum;
import com.mxw.protocol.response.nonFungibleToken.NFTokenStatusFee;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.protocol.response.nonFungibleToken.*;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;

public class Test0700NonFungibleToken {
    private HttpService httpService;
    private Provider jsonRpcProvider;

    private Wallet wallet;
    private Wallet provider;
    private Wallet issuer;
    private Wallet middleware;

    private final String feeCollector = "mxw1qgwzdxf66tp5mjpkpfe593nvsst7qzfxzqq73d";

    private String symbol;
    private String itemID;

    private final int ATTEMPT = 10;
    private final int INTERVAL = 5000;

    @Before
    public void before() throws Exception {
        this.httpService = new HttpService(TestConfig.HTTP_SERVICE_URL, false);
        this.jsonRpcProvider = new JsonRpcProvider(this.httpService,
                new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));

        this.wallet = Wallet.fromMnemonic("pill maple dutch predict bulk goddess nice left paper heart loan fresh");
        this.wallet.connect(jsonRpcProvider);
        System.out.println(wallet.getAddress());

        this.provider = Wallet.fromMnemonic("grocery focus dad before desert misery swear hole hockey domain betray annual");
        this.provider.connect(jsonRpcProvider);
        this.issuer = Wallet.fromMnemonic("tray rail nature multiply purpose theme federal under soda victory ladder over");
        this.issuer.connect(jsonRpcProvider);
        this.middleware = Wallet.fromMnemonic("lava toe off also pepper divide dove next double suit list frost");
        this.middleware.connect(jsonRpcProvider);

        this.symbol = "NFT" + Integer.toHexString(Math.abs(new Random().nextInt()));
//        this.symbol = "NFT2855316";
        this.itemID = "Item-" + Integer.toHexString(Math.abs(new Random().nextInt()));
//        this.itemID = "Item-6eec0f51";
    }

    @Test
    public void startNonFungibleTokenTest() throws Exception {
//        createNonFungibleToken();
//        refreshNonFungibleToken();
//        approveStatusNonFungibleToken();
//        mintNonFungibleTokenItem();
//        burnNonFungibleTokenItem();
//        transferNonFungibleTokenItem();
//        endorseNonFungibleTokenItem();
//        transferOwnershipNonFungibleToken();
//        updateStatusNonFungibleToken(NonFungibleTokenEnum.NonFungibleTokenStatusActions.APPROVE_TRANSFER_TOKEN_OWNERSHIP);
//        updateStatusNonFungibleToken(NonFungibleTokenEnum.NonFungibleTokenStateActions.FREEZE);
//        acceptOwnershipNonFungibleToken();
    }

    public void createNonFungibleToken() throws Exception {
        TransactionResponse response = NonFungibleToken.create(
                new NFTokenCreate(
                        feeCollector,
                        "1",
                        "MY" + symbol,
                        "metadata",
                        "properties",
                        this.symbol
                ), this.wallet, null);

        System.out.println("Wait for create transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Create NFT success ... \n");
    }

    public void refreshNonFungibleToken() throws Exception {
        System.out.println("refresh: " + NonFungibleToken.fromSymbol(this.symbol, this.wallet, null));
    }

    public void mintNonFungibleTokenItem() throws Exception {
        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .mint(new NFTokenMint(
                                this.symbol,
                                this.itemID,
                                "item properties",
                                "item metadata"),
                        this.wallet.getAddress(),
                        null);

        System.out.println("Wait for mint transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Mint NFT item success ... \n");
    }

    public void burnNonFungibleTokenItem() throws Exception {
        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .burn(new NFTokenBurn(
                                this.symbol,
                                this.itemID),
                        null);
        System.out.println("Wait for burn transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, 5000);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Burn NFT item success ... \n");
    }

    public void transferNonFungibleTokenItem() throws Exception {
        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .transfer(new NFTokenTransferItem(
                                this.symbol,
                                this.itemID),
                        this.wallet.getAddress(),
                        null);
        System.out.println("Wait for transfer transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Transfer NFT item success ... \n");
    }

    public void endorseNonFungibleTokenItem() throws Exception {
        String metadata = "";
        for (int i = 0; i < 85; i++)
            metadata += "跳";

        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .endorse(new NFTokenEndorse(
                                this.symbol,
                                this.itemID,
                                metadata),
                        null);
        System.out.println("Wait for endorse transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Endorse NFT item success ... \n");
    }

    private void approveStatusNonFungibleToken() throws Exception {
        NFTokenStatusFee[] tokenFees = new NFTokenStatusFee[]{
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.TRANSFER, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.MINT, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.BURN, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.TRANSFER_OWNERSHIP, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.ACCEPT_OWNERSHIP, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.ENDORSE, "default"),
                new NFTokenStatusFee(NonFungibleTokenEnum.NFTokenActions.UPDATE_NFT_ENDORSER_LIST, "default"),
        };

        NFTokenStatusPayload payload =
                NonFungibleToken.createNFTokenStatusPayload(this.symbol,
                        NonFungibleTokenEnum.NonFungibleTokenStatusActions.APPROVE, this.provider,
                        new Bundle()
                                .put("tokenFees", tokenFees)
//                                .put("endorserList", new String[]{
//                                        middleware.getAddress(),
//                                        wallet.getAddress()
//                                })
                                .put("endorserList", null)
                                .put("endorserListLimit", "1")
                                .put("mintLimit", "20")
                                .put("transferLimit", "20")
                                .put("burnable", true)
                                .put("transferable", true)
                                .put("modifiable", true)
                                .put("pub", false));

        NFTokenStatusTransaction transaction = NonFungibleToken.signNonFungibleTokenStatusTransaction(
                payload, this.issuer, null);

        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.middleware, null)
                .sendNonFungibleTokenStatusTransaction(transaction, null);

        System.out.println("Wait for status 'approve' update transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("NFT status 'approve' update success ... \n");
    }

    private void updateStatusNonFungibleToken(NonFungibleTokenEnum.NonFungibleTokenStatusActions status) throws Exception {
        NFTokenStatusPayload payload =
                NonFungibleToken.createNFTokenStatusPayload(this.symbol,
                        status, this.provider, null);

        NFTokenStatusTransaction transaction = NonFungibleToken.signNonFungibleTokenStatusTransaction(
                payload, this.issuer, null);

        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.middleware, null)
                .sendNonFungibleTokenStatusTransaction(transaction, null);

        System.out.println("Wait for status '" + status.toString() + "' update transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("NFT status '" + status.toString() + "' update success ... \n");
    }


    public void transferOwnershipNonFungibleToken() throws Exception {
        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .transferOwnership(wallet.getAddress(),
                        null);
        System.out.println("Wait for transfer ownership transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, 5000);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Transfer NFT ownership success ... \n");
    }

    public void acceptOwnershipNonFungibleToken() throws Exception {
        TransactionResponse response = NonFungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .acceptOwnership(null);
        System.out.println("Wait for accept ownership transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, 5000);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Accept NFT ownership success ... \n");
    }

    private Optional<TransactionReceipt> waitForTransaction(String hash, int attempt, int sleepDuration) throws InterruptedException {
        Optional<TransactionReceipt> transactionReceipt = getTransactionReceipt(hash);
        for (int i = 0; i < attempt; i++) {
            if (!transactionReceipt.isPresent()) {
                Thread.sleep(sleepDuration);
                transactionReceipt = getTransactionReceipt(hash);
            } else {
                break;
            }
        }
        return transactionReceipt;
    }

    private Optional<TransactionReceipt> getTransactionReceipt(String hash) {
        try {
            return Optional.of(this.jsonRpcProvider.getTransactionReceipt(hash, Object.class));
        } catch (TransactionException ex) {
            return Optional.empty();
        }
    }
}
