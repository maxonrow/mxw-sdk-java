package com.mxw.wallet;

import com.mxw.TestConfig;
import com.mxw.Wallet;
import com.mxw.crypto.SecretStorageUtils;
import com.mxw.crypto.SigningKey;
import com.mxw.crypto.WalletFile;
import com.mxw.exceptions.CipherException;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.tx.DefaultTransactionManager;
import com.mxw.utils.Convert;
import com.mxw.utils.Strings;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

public class WalletTest {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

    @Ignore
    @Test
    public void testTransfer() {
        String privateKey = TestConfig.PRIVATE_KEY_STRING;
        String toAddress = TestConfig.TO_ADDRESS;
        Wallet wallet = new Wallet(privateKey);
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
        for(int i=0; i < 100; i++) {
           Wallet w = Wallet.createNewWallet();
           String address = w.getAddress();
            TransactionResponse response = wallet.transfer(address, amount, "this is a memo for " + address);
            Assert.assertFalse(Strings.isEmpty(response.getHash()));
        }
    }

    @Test
    public void testWalletFromMnemonic() {
        String mnemonic = TestConfig.MNEMONIC;
        String privateKey = TestConfig.PRIVATE_KEY_STRING;

        Wallet wallet =  Wallet.fromMnemonic(mnemonic, Optional.empty());
        System.out.println(wallet.getPrivateKey());
        System.out.println(wallet.getAddress());
        Assert.assertEquals(privateKey, wallet.getPrivateKey());
    }


    @Test
    public void testGetWalletFile() throws CipherException {
        WalletFile walletFile = SecretStorageUtils.getWalletFileFromJson(TestConfig.JSON_WALLET_STRING);
        Assert.assertEquals(walletFile.getAddress(), TestConfig.JSON_WALLET_ADDRESS);
    }

    @Test
    public void testDecryptJson() throws CipherException {

        Wallet wallet = Wallet.fromEncryptedJson(TestConfig.JSON_WALLET_STRING,TestConfig.ENCRYPT_PASSWORD);
        Assert.assertEquals(wallet.getAddress(),TestConfig.JSON_WALLET_ADDRESS);
    }


}
