package com.mxw.crypto;

import com.mxw.Constants;
import org.junit.Assert;
import org.junit.Test;


public class MnemonicTest {

    @Test
    public void testCorrectMnemonic() {
        String mnemonic = "zero what glove tone wise throw settle west iron jump exhaust clay";
        String privateKey = "0x11791686731e77f07758b86139837b42401be93ac6638ade5fc266403351652e";

        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Constants.DefaultHDPath);
        Assert.assertEquals(privateKey, bip44Keypair.getPrivateKey());
    }


}
