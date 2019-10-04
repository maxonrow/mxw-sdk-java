package com.mxw;

import com.mxw.crypto.SigningKey;
import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.response.TransactionFee;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.tx.DefaultTransactionManager;
import com.mxw.tx.FastTransactionManager;
import com.mxw.tx.TransactionManager;
import com.mxw.utils.Convert;
import com.mxw.utils.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class Test0000Ignore {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

    private String privateKeyString;
    private String toAddress;
    private String mnemonic;

    @Before
    public void initialize(){
        mnemonic = TestConfig.MNEMONIC;
        SigningKey key = SigningKey.fromMnemonic(mnemonic);
        privateKeyString = key.getPrivateKey();
        toAddress = TestConfig.TO_ADDRESS;
    }

    @Ignore
    @Test
    public void testTransfer() {
        Wallet wallet = new Wallet(privateKeyString);
        wallet = wallet.connect(jsonRpcProvider);
        BigInteger amount = Convert.toCIN("1", Convert.Unit.MXW).toBigIntegerExact();
        TransactionResponse response = wallet.transfer(toAddress, amount, "this is a memo");
        Assert.assertFalse(Strings.isEmpty(response.getHash()));
    }

    @Ignore
    @Test
    public void testBulkTransfer() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String privateKey = TestConfig.PRIVATE_KEY_STRING;

        SigningKey signingKey = new SigningKey(privateKey);
        Wallet wallet = new Wallet(signingKey, jsonRpcProvider, new DefaultTransactionManager(jsonRpcProvider, signingKey));
        BigInteger amount = Convert.toCIN("1", Convert.Unit.MXW).toBigIntegerExact();
        for(int i=0; i < 5; i++) {
            Wallet w = Wallet.createNewWallet();
            String address = w.getAddress();
            TransactionResponse response = wallet.transfer(address, amount, "this is a memo for " + address);
            Assert.assertFalse(Strings.isEmpty(response.getHash()));
        }
    }

    @Ignore
    @Test
    public void testBulkTransferShouldFail() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        SigningKey key = SigningKey.fromMnemonic(mnemonic);
        DefaultTransactionManager transactionManager = new DefaultTransactionManager(jsonRpcProvider, key);

        for (TransactionRequest req : constructSignedRequest(transactionManager)) {
            try{
                transactionManager.sendTransaction(req);
            }catch (TransactionException ex) {
                Assert.assertEquals(ex.getMessage(),"signature verification failed");
                return;
            }
        }
        Assert.fail("no exception");
    }

    @Ignore
    @Test
    public void testBulkTransferShouldSuccess() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        SigningKey key = SigningKey.fromMnemonic(mnemonic);
        FastTransactionManager transactionManager = new FastTransactionManager(jsonRpcProvider, key);
        for (TransactionRequest req : constructSignedRequest(transactionManager)) {
            transactionManager.sendTransaction(req);
        }
    }

    private List<TransactionRequest> constructSignedRequest(TransactionManager transactionManager) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        List<TransactionRequest> requests = new ArrayList<>();
        for(int i=0;i < 5;i++){
            Wallet wallet = Wallet.createNewWallet();
            BigInteger amount = Convert.toCIN("1", Convert.Unit.MXW).toBigIntegerExact();
            String toAddress = wallet.getAddress();
            TransactionRequest request = transactionManager.createTransaction(new BankSendBuilder(transactionManager.getFromAddress(),toAddress,amount, "this is a memo " + toAddress));
            transactionManager.signRequest(request);
            requests.add(request);
        }
        return requests;
    }

    @Ignore
    @Test
    public void signTransaction() {

        Wallet wallet = new Wallet(privateKeyString);

        String toAddress = this.jsonRpcProvider.resolveName(this.toAddress);

        BigInteger amount = BigInteger.valueOf(1);

        TransactionRequest request = this.jsonRpcProvider.getTransactionRequest("bank","bank-send", new BankSendBuilder(wallet.getAddress(), toAddress, amount, "1"));
        TransactionFee fee = this.jsonRpcProvider.getTransactionFee(null, null, request);

        request.getValue().setFee(fee);

        wallet.connect(jsonRpcProvider);

        if(request.getNonce()==null) {
            request.setNonce(jsonRpcProvider.getTransactionCount(wallet.getAddress()));
        }

        if(Strings.isEmpty(request.getChainId())){
            request.setChainId(jsonRpcProvider.getNetwork().getChainId());
        }

        String signedTransaction = wallet.sign(request);
        TransactionResponse transactionResponse = jsonRpcProvider.sendTransaction(signedTransaction, false);

        String hash = transactionResponse.getHash();
        System.out.println(hash);
        Assert.assertNotNull(hash);
    }


    @Ignore
    @Test
    public void transferButNotEnoughBalanceForFee(){

        Wallet wallet = new Wallet(privateKeyString);

        BigInteger amount = Convert.toCIN(new BigDecimal("1000000000000000000000"), Convert.Unit.MXW).toBigIntegerExact();

        TransactionRequest request = this.jsonRpcProvider.getTransactionRequest("bank","bank-send", new BankSendBuilder(wallet.getAddress(), toAddress, amount, "1"));
        TransactionFee fee = this.jsonRpcProvider.getTransactionFee(null, null, request);
        request.getValue().setFee(fee);

        wallet.connect(jsonRpcProvider);

        if(request.getNonce()==null) {
            request.setNonce(jsonRpcProvider.getTransactionCount(wallet.getAddress()));
        }

        if(Strings.isEmpty(request.getChainId())){
            request.setChainId(jsonRpcProvider.getNetwork().getChainId());
        }

        try{
            String signedTransaction = wallet.sign(request);
            TransactionResponse transactionResponse = jsonRpcProvider.sendTransaction(signedTransaction, false);

        }catch (TransactionException ex) {
            Assert.assertEquals(ex.getMessage(),"insufficient funds");
            return;
        }

        Assert.fail("no exception");
    }


}
