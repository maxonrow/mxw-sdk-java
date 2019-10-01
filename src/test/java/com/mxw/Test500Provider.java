package com.mxw;

import com.mxw.networks.Network;
import com.mxw.networks.Networks;

import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.response.*;

import com.mxw.providers.JsonRpcProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class Test500Provider {

    private HttpService httpService;
    private JsonRpcProvider jsonRpcProvider;

    private Wallet wallet;

    private String privateKey;
    private String toAddress;

    @Before
    public void before() {

        this.httpService = new HttpService(TestConfig.HTTP_SERVICE_URL, false);
        this.privateKey = TestConfig.PRIVATE_KEY_STRING;
        this.toAddress = TestConfig.TO_ADDRESS;
        this.jsonRpcProvider = new JsonRpcProvider(this.httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));
    }

    @Test
    public void testInstantiateNetwork() {

        Network testnet = Networks.ALLOYS.getNetwork();
        Assert.assertEquals(testnet.getChainId(),"alloys");

        Network mainnet = Networks.MAINNET.getNetwork();
        Assert.assertEquals(mainnet.getChainId(),"maxonrow");

        Network homestead = Networks.HOMESTEAD.getNetwork();
        Assert.assertEquals(homestead.getChainId(), "maxonrow");

        Network n = Network.getNetwork("testnet");
        Assert.assertEquals(n.getChainId(),"testnet");
    }

    @Test
    public void getAccountState(){
        AccountState accountState = jsonRpcProvider.getAccountState("mxw1mklypleqjhemrlt6z625rzqa0jl6namdmmqnx4");
        assert(accountState == null);
    }

    @Test
    public void getBalance(){
        BigInteger balance = jsonRpcProvider.getBalance("mxw1mklypleqjhemrlt6z625rzqa0jl6namdmmqnx4");
        assert(balance.compareTo(BigInteger.ZERO) == 0);
    }

    @Test
    public void getBlockNumber(){
        BigInteger blockNumber = jsonRpcProvider.getBlockNumber();
        assert(blockNumber.compareTo(BigInteger.ZERO) > 0);
    }

    @Test
    public void getTransaction(){
        TransactionReceipt transactionReceipt = jsonRpcProvider.getTransaction(TestConfig.HTTP_TEST_DECODE_HASH);
        assert(transactionReceipt != null);
    }

    @Test
    public void getTansactionReceipt(){
        TransactionReceipt transactionReceipt = jsonRpcProvider.getTransactionReceipt(TestConfig.HTTP_TEST_DECODE_HASH, TransactionRequest.class);
        assert(transactionReceipt != null);
    }

    @Test
    public void getStatus(){

        Status status = jsonRpcProvider.getStatus();

        NodeInfo nodeInfo = status.getNodeInfo();
        Status.ValidatorInfo validatorInfo = status.getValidatorInfo();
        Status.SyncInfo syncInfo = status.getSyncInfo();

        assert(nodeInfo != null && validatorInfo != null && syncInfo != null);

    }

    @Test
    public void getAccountNumberWithNonExistsAddress(){

        BigInteger accountNumber = jsonRpcProvider.getAccountNumber("mxw18nyjc5sxz0tlndf8uslyj6k9leha59vc23djl6");
        System.out.println(accountNumber);

        assert(accountNumber.compareTo(BigInteger.ZERO) == 0);
    }

    //Check KYC whitelist status

    @Test
    public void getTransctionFee(){

        Wallet wallet = new Wallet(privateKey);

        String toAddress = this.jsonRpcProvider.resolveName(this.toAddress);

        BigInteger amount = BigInteger.valueOf(1);

        TransactionRequest request = this.jsonRpcProvider.getTransactionRequest("bank","bank-send", new BankSendBuilder(wallet.getAddress(), toAddress, amount, "1"));
        TransactionFee fee = this.jsonRpcProvider.getTransactionFee(null, null, request);

        BigInteger gas = fee.getGas();
        System.out.println(gas.toString());

        assert(gas.compareTo(BigInteger.ZERO) >= 0);
    }

    // getTransactionFeeSetting
    // getAliasAppliction

}
