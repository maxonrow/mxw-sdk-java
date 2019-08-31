package com.mxw;

import com.mxw.crypto.Bip32ECKeyPair;
import com.mxw.crypto.MnemonicUtils;

import com.mxw.crypto.Sign;
import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;

import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.BlockTagName;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.response.TransactionFee;
import com.mxw.protocol.response.TransactionResponse;

import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;

import com.mxw.utils.Convert;
import com.mxw.utils.Strings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Test0200Wallet {

    private HttpService httpService;
    private Provider jsonRpcProvider;

    private Wallet wallet;

    private String privateKey;
    private String toAddress;

    @Before
    public void before() {

        this.httpService =  new HttpService(TestConfig.HTTP_SERVICE_URL, false);
        this.privateKey = TestConfig.PRIVATE_KEY_STRING;
        this.toAddress = TestConfig.TO_ADDRESS;
        this.jsonRpcProvider = new JsonRpcProvider(this.httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));
        this.wallet = new Wallet(privateKey, jsonRpcProvider);
    }

    @Test
    public void createRandom() throws Exception {

        Wallet wallet1 = Wallet.createNewWallet();
        Wallet wallet2 = Wallet.createNewWallet();

        String privateKey1 = wallet1.getPrivateKey();
        String privateKey2 = wallet2.getPrivateKey();

        assert(privateKey1 != null && privateKey2 != null);
    }

    // public void createRandomWithLanguage(){
    // public void CreateRandomWithDifferentNumberOfWords(){
    // public void createRandomWithMultiLanguage(){
    // public void createRandomWithMultiLanguagesAndDifferentNumberOfWords(){

    @Test
    public void createFromMnemonic(){

        String mnemonic = TestConfig.MNEMONIC;
        String privateKey = TestConfig.PRIVATE_KEY_STRING;

        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);

        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Constants.DefaultHDPath);

        Assert.assertEquals(privateKey, bip44Keypair.getPrivateKey());
    }

    // public void creaateFromMnemonicWtihMultiLanguagesAndDifferentNumberOfWordsAndDifferentPath(){

    @Test
    public void createWithJSONRPCProvider() throws Exception{

        Wallet wallet = Wallet.createNewWallet();
        wallet = wallet.connect(jsonRpcProvider);

        assert(wallet.getProvider() != null);
    }

    /*
    @Test
    public void lookupAddress() throws Exception{

        Wallet wallet = new Wallet(this.privateKey);
        wallet = wallet.connect(jsonRpcProvider);

        String lookupAddress = this.jsonRpcProvider.lookupAddress(wallet.getAddress());
        System.out.println(lookupAddress);

        //assert() != null);
    }*/
    @Ignore
    @Test
    public void transfer(){
        TransactionResponse response = wallet.connect(jsonRpcProvider).transfer(toAddress, BigInteger.valueOf(1000000000000L), "testing");
        System.out.println(response.getHash());
        Assert.assertFalse(Strings.isEmpty(response.getHash()));
    }

    @Test
    public void airDropTransferWithZeroFee(){

        String mnemonic = "maid oval sand actress work push mention never thunder defense cigar train";
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);

        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Constants.DefaultHDPath);

        System.out.println("Airdrop private key = " + bip44Keypair.getPrivateKey());

        Wallet wallet = new Wallet(bip44Keypair.getPrivateKey());

        BigInteger amount = BigInteger.valueOf(1);

        TransactionRequest request = this.jsonRpcProvider.getTransactionRequest("bank","bank-send", new BankSendBuilder(wallet.getAddress(), toAddress, amount, "1"));
        TransactionFee fee = this.jsonRpcProvider.getTransactionFee(null, null, request);

        BigInteger gas = fee.getGas();

        assert(gas.compareTo(BigInteger.ZERO) == 0);
    }

    @Ignore
    @Test
    public void transferButNotEnoughBalanceForFee(){

        Wallet wallet = new Wallet(TestConfig.PRIVATE_KEY_STRING);

        BigInteger amount = Convert.toCIN(new BigDecimal("10000000000000000000"), Convert.Unit.MXW).toBigIntegerExact();

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

    @Test
    public void getAccountNumber(){

        Wallet wallet = new Wallet(this.privateKey);
        wallet.connect(jsonRpcProvider);

        BigInteger accountNumber = wallet.getAccountNumber();
        System.out.println("Account number = " + accountNumber);

        assert(accountNumber.compareTo(BigInteger.ZERO) >= 0);
    }

    @Test
    public void getBalance(){

        Wallet wallet = new Wallet(this.privateKey);
        wallet.connect(jsonRpcProvider);

        System.out.printf("Balance = " + wallet.getBalance());

        assert(wallet.getBalance().compareTo(BigInteger.ZERO) >= 0);
    }

    @Test
    public void getTransactionCountNounce(){

        Wallet wallet = new Wallet(this.privateKey);
        wallet.connect(jsonRpcProvider);
        BigInteger transactionCount = wallet.getTransactionCount(BlockTagName.LATEST);

        System.out.println("Transaction count = " + transactionCount);

        assert(transactionCount.compareTo(BigInteger.ZERO) >= 0);

    }

    @Test
    public void signMessage(){

        Wallet wallet = new Wallet(privateKey);

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

        assert(signedTransaction != null);
    }

    @Ignore
    @Test
    public void signTransaction() throws Exception{

        Wallet wallet = new Wallet(privateKey);

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
        assert(hash != null);
    }

    // signTransactionWtihAnonymousAttributes
    // cleanUpRpcListener
    // encryptWalletWithMultiLanguage
    // decryptWalletWithMultiLanguage
    // computedSharedSecret

}
