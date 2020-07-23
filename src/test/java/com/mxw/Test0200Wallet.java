package com.mxw;

import com.mxw.crypto.Bip32ECKeyPair;
import com.mxw.crypto.MnemonicUtils;
import com.mxw.exceptions.CipherException;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.BlockTagName;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.response.TransactionFee;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.utils.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

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
    public void testSignDataMessage() throws CipherException {
        String message = "Hello World";
        String hex =  this.wallet.signMessage(message.getBytes(), true);
        Assert.assertEquals(hex, "0x034da2e201c6909b27704ff4702db71477e3dfc8c6008f9c74185e2c084066ba574e8c354274079952d9868869751e009baf8b142f5fc8bf51579df5aac23345");
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

        System.out.println("Balance = " + wallet.getBalance());

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

    @Test
    public void testComputeSharedSecret() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException {
        Wallet wallet1 = Wallet.createNewWallet();
        Wallet wallet2 = Wallet.createNewWallet();
        String sharedSecret1 = wallet1.computeSharedSecret(wallet2.getCompressedPublicKey());
        String sharedSecret2 = wallet2.computeSharedSecret(wallet1.getCompressedPublicKey());
        String sharedSecret3 = wallet1.computeSharedSecret(wallet2.getPublicKey());
        String sharedSecret4 = wallet2.computeSharedSecret(wallet1.getPublicKey());
        Assert.assertEquals(sharedSecret1, sharedSecret2);
        Assert.assertEquals(sharedSecret3, sharedSecret4);
        Assert.assertEquals(sharedSecret1, sharedSecret4);
    }


    // signTransactionWtihAnonymousAttributes
    // cleanUpRpcListener
    // encryptWalletWithMultiLanguage
    // decryptWalletWithMultiLanguage
    // computedSharedSecret

}
