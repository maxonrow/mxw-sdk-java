package com.mxw;

import com.mxw.exceptions.TransactionException;
import com.mxw.fungibleToken.FungibleToken;
import com.mxw.fungibleToken.FungibleTokenEnum;
import com.mxw.networks.Network;
import com.mxw.nonFungibleToken.NonFungibleToken;
import com.mxw.nonFungibleToken.NonFungibleTokenEnum;
import com.mxw.protocol.common.Bundle;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.protocol.response.fungibleToken.*;
import com.mxw.protocol.response.nonFungibleToken.*;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;

public class Test0700FungibleToken {
    private HttpService httpService;
    private Provider jsonRpcProvider;

    private Wallet wallet;
    private Wallet provider;
    private Wallet issuer;
    private Wallet middleware;

    private final String feeCollector = "mxw1qgwzdxf66tp5mjpkpfe593nvsst7qzfxzqq73d";

    private String symbol;

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

        this.provider = Wallet.fromMnemonic("mother paddle fire dolphin nuclear giggle fatal crop cupboard close abandon truck");
        this.provider.connect(jsonRpcProvider);
        this.issuer = Wallet.fromMnemonic("dynamic car culture shell kiwi harsh tilt boost vote reopen arrow moon");
        this.issuer.connect(jsonRpcProvider);
        this.middleware = Wallet.fromMnemonic("hospital item sad baby mass turn ability exhibit obtain include trip please");
        this.middleware.connect(jsonRpcProvider);

//        this.symbol = "FT" + Integer.toHexString(Math.abs(new Random().nextInt()));
        this.symbol = "FT4c4cf739";
    }

    @Test
    public void startFungibleTokenTest() throws Exception {
//        createFungibleToken();
//        refreshFungibleToken();
//        approveStatusFungibleToken();
//        burnFungibleToken();
//        mintFungibleToken();
//        updateStatusFungibleToken(FungibleTokenEnum.FungibleTokenStatusActions.UNFREEZE);
//        transferFungibleToken();
    }

    public void createFungibleToken() throws Exception {
        TransactionResponse response = FungibleToken.create(
                new FungibleTokenCreate(
                        feeCollector,
                        "1",
                        "MY" + symbol,
                        "",
                        this.symbol,
                        "1",
                        true,
                        "100000000000000000000000000"
                ), this.wallet, null);

        System.out.println("Wait for create transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Create Fungible Token success ... \n");
    }

    public void refreshFungibleToken() throws Exception {
        System.out.println("refresh: " + FungibleToken.fromSymbol(this.symbol, this.wallet, null));
    }

    private void approveStatusFungibleToken() throws Exception {
        FungibleTokenStatusFee[] tokenFees = new FungibleTokenStatusFee[]{
                new FungibleTokenStatusFee(FungibleTokenEnum.FungibleTokenActions.TRANSFER, "default"),
                new FungibleTokenStatusFee(FungibleTokenEnum.FungibleTokenActions.TRANSFER_OWNERSHIP, "default"),
                new FungibleTokenStatusFee(FungibleTokenEnum.FungibleTokenActions.ACCEPT_OWNERSHIP, "default"),
                new FungibleTokenStatusFee(FungibleTokenEnum.FungibleTokenActions.BURN, "default"),
        };

        FungibleTokenStatusPayload payload =
                FungibleToken.createFungibleTokenStatusPayload(this.symbol,
                        FungibleTokenEnum.FungibleTokenStatusActions.APPROVE, this.provider,
                        new Bundle()
                                .put("tokenFees", tokenFees)
                                .put("endorserList", null)
                                .put("burnable", true));

        FungibleTokenStatusTransaction transaction = FungibleToken.signFungibleTokenStatusTransaction(
                payload, this.issuer, null);

        TransactionResponse response = FungibleToken.fromSymbol(this.symbol, this.middleware, null)
                .sendFungibleTokenStatusTransaction(transaction, null);

        System.out.println("Wait for status 'approve' update transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Fungible Token status 'approve' update success ... \n");
    }

    public void burnFungibleToken() throws Exception {
        TransactionResponse response = FungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .burn(new FungibleTokenBurn(
                                this.symbol,
                                this.wallet.getBalance().toString()),
                        null);
        System.out.println("Wait for burn transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, 5000);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Burn Fungible Token success ... \n");
    }

    private void updateStatusFungibleToken(FungibleTokenEnum.FungibleTokenStatusActions status) throws Exception {
        FungibleTokenStatusPayload payload =
                FungibleToken.createFungibleTokenStatusPayload(this.symbol,
                        status, this.provider, null);

        FungibleTokenStatusTransaction transaction = FungibleToken.signFungibleTokenStatusTransaction(
                payload, this.issuer, null);

        TransactionResponse response = FungibleToken.fromSymbol(this.symbol, this.middleware, null)
                .sendFungibleTokenStatusTransaction(transaction, null);

        System.out.println("Wait for status '" + status.toString() + "' update transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("NFT status '" + status.toString() + "' update success ... \n");
    }

    public void transferFungibleToken() throws Exception {
        TransactionResponse response = FungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .transfer(new FungibleTokenTransfer(
                                this.symbol,
                                this.wallet.getBalance().toString()),
                        this.wallet.getAddress(),
                        null);
        System.out.println("Wait for transfer transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Transfer Fungible Token item success ... \n");
    }

    public void mintFungibleToken() throws Exception {
        TransactionResponse response = FungibleToken.fromSymbol(this.symbol, this.wallet, null)
                .mint(new FungibleTokenMint(
                                this.symbol,
                                "10000000000000000000000000"),
                        this.wallet.getAddress(),
                        null);

        System.out.println("Wait for mint transaction ... " + response.getHash());
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), ATTEMPT, INTERVAL);
        Assert.assertTrue(receipt.isPresent());
        assert (receipt.get().getStatus() == 1);

        System.out.println("Mint Fungible Token success ... \n");
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
