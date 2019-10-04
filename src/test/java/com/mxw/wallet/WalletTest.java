package com.mxw.wallet;

import com.mxw.TestConfig;
import com.mxw.Wallet;
import com.mxw.crypto.SecretStorageUtils;
import com.mxw.crypto.WalletFile;
import com.mxw.exceptions.CipherException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class WalletTest {


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


}
