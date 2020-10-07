package com.mxw.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.Constants;
import com.mxw.TestConfig;
import com.mxw.Wallet;
import com.mxw.crypto.*;
import com.mxw.exceptions.CipherException;
import com.mxw.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Optional;

import static com.mxw.crypto.Bip32ECKeyPair.HARDENED_BIT;


public class WalletTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testWalletFromMnemonic() {
        String mnemonic = TestConfig.MNEMONIC;
        String privateKey = TestConfig.PRIVATE_KEY_STRING;

        Wallet wallet =  Wallet.fromMnemonic(mnemonic, Optional.empty());
        Assert.assertEquals(privateKey, wallet.getPrivateKey());
        Assert.assertEquals(wallet.getAddress(), TestConfig.PRIVATE_KEY_ADDRESS);
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

    @Test
    public void testEncryptWithoutMnemonic() throws CipherException, JsonProcessingException {
        Wallet wallet = new Wallet(TestConfig.PRIVATE_KEY_STRING);
        WalletFile walletFile = wallet.EncryptWallet(TestConfig.ENCRYPT_PASSWORD);
        String json = wallet.EncryptWalletJson(TestConfig.ENCRYPT_PASSWORD);
        SigningKey signingKey = SecretStorage.decryptToSignKey(TestConfig.ENCRYPT_PASSWORD, walletFile);
        Assert.assertEquals(wallet.getSigningKey(), signingKey);
        Assert.assertEquals(wallet.getPrivateKey(), signingKey.getPrivateKey());
    }

    @Test
    public void testEncryptWithMnemonic() throws CipherException, JsonProcessingException {
        String mnemonic = TestConfig.MNEMONIC;
        SigningKey signingKey = SigningKey.fromMnemonic(mnemonic);
        WalletFile walletFile = SecretStorage.createEncryptedWallet(TestConfig.ENCRYPT_PASSWORD, signingKey);
        Wallet wallet = Wallet.fromEncryptedJson(objectMapper.writeValueAsString(walletFile), TestConfig.ENCRYPT_PASSWORD);
        Assert.assertNotNull(walletFile.getMxw());
        Assert.assertEquals(walletFile.getAddress(), signingKey.getAddress());
        Assert.assertEquals(wallet.getSigningKey().getPrivateKey(), signingKey.getPrivateKey());
        Assert.assertEquals(wallet.getSigningKey().getMnemonic(), mnemonic);

    }

    @Test
    public void testDecryptJsonWithMxwMetadata() throws CipherException {
        WalletFile walletFile = SecretStorageUtils.getWalletFileFromJson(TestConfig.JSON_WALLET_STRING_WITH_MXW);
        SigningKey signingKey = SecretStorage.decryptToSignKey(TestConfig.ENCRYPT_PASSWORD, walletFile);
        Assert.assertEquals(walletFile.getAddress(), signingKey.getAddress());
        Wallet wallet = Wallet.fromEncryptedJson(TestConfig.JSON_WALLET_STRING_WITH_MXW, TestConfig.ENCRYPT_PASSWORD);
        Assert.assertEquals(signingKey.getPrivateKey(), wallet.getPrivateKey());
        Assert.assertEquals(signingKey.getMnemonic(),TestConfig.MNEMONIC);
    }

    @Test
    public void testAes256ctrDecryptJson() throws CipherException {
        String privateKey = "0x50d0a9e6ac60d7fadfbcfba599f9f2a7afd597098882dcad3406ae1eb62bc4dd";
        String json = "{\"address\":\"mxw1qcxyz60rcfua5uveddf8hkptf3vpgw8n7h39fh\",\"id\":\"d7737872-85b3-4818-b128-31fbef465237\",\"version\":3,\"Crypto\":{\"cipher\":\"aes-256-ctr\",\"cipherparams\":{\"iv\":\"d5872d05c3e8f9aaea57def14fafd23e\"},\"ciphertext\":\"e6bffaa01fe72374c08e9a3ec4f0bd7c64a8805da433a19e33e12e6353550c1d\",\"kdf\":\"scrypt\",\"kdfparams\":{\"salt\":\"1f1109bea4fde240b97047e2d7312237669ef88424cea02a0082de2e96b1feb2\",\"n\":131072,\"dklen\":48,\"p\":1,\"r\":8},\"mac\":\"73c1995e35a4cb88a0c2ba3db29ff439fe9c371efab4c003a6bc7edded97411e\"},\"x-mxw\":{\"client\":\"mxw-sdk\",\"filename\":\"UTC--2020-07-22T09-38-15.0Z--mxw1qcxyz60rcfua5uveddf8hkptf3vpgw8n7h39fh\",\"mnemonicCounter\":\"8a4d8773c6073e1d7e04ec28e9b1d92d\",\"mnemonicCiphertext\":\"efc7112ff80d0a2de063735383f46a64\",\"path\":\"m/44'/376'/0'/0/0\",\"locale\":\"en\",\"version\":\"0.1\"}}";
        Wallet wallet = Wallet.fromEncryptedJson(json,"any strong password");
        Assert.assertEquals(privateKey,wallet.getPrivateKey());
    }


}
