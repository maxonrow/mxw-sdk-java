package com.mxw.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.TestConfig;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.utils.Numeric;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;

import static com.mxw.crypto.Hash.sha256;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class SecretStorageUtilsTest {

    private File tempDir;


    static final BigInteger PRIVATE_KEY = Numeric.toBigInt(TestConfig.PRIVATE_KEY_STRING);
    static final ECKeyPair KEY_PAIR = new ECKeyPair(PRIVATE_KEY);

    public static final SigningKey SIGNING_KEY = new SigningKey(KEY_PAIR);

    private ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    @Before
    public void setUp() throws Exception {
        tempDir = createTempDir();
    }

    @After
    public void tearDown() throws Exception {
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
        tempDir.delete();
    }

    @Test
    public void testGenerateBip39Wallets() throws Exception {
        Bip39Wallet wallet = SecretStorageUtils.generateBip39Wallet(TestConfig.ENCRYPT_PASSWORD, tempDir);
        byte[] seed = MnemonicUtils.generateSeed(wallet.getMnemonic(), TestConfig.ENCRYPT_PASSWORD);
        SigningKey signingKey = new SigningKey(ECKeyPair.create(sha256(seed)));

        assertEquals(signingKey, SecretStorageUtils.loadBip39SigningKey(TestConfig.ENCRYPT_PASSWORD, wallet.getMnemonic()));
    }

    @Test
    public void testGenerateBip39WalletFromMnemonic() throws Exception {
        Bip39Wallet wallet =
                SecretStorageUtils.generateBip39WalletFromMnemonic(TestConfig.ENCRYPT_PASSWORD, TestConfig.MNEMONIC, tempDir);
        byte[] seed = MnemonicUtils.generateSeed(wallet.getMnemonic(), TestConfig.ENCRYPT_PASSWORD);
        SigningKey signingKey = new SigningKey(ECKeyPair.create(sha256(seed)));

        assertEquals(signingKey, SecretStorageUtils.loadBip39SigningKey(TestConfig.ENCRYPT_PASSWORD, wallet.getMnemonic()));
    }

    @Test
    public void testGenerateFullNewWalletFile() throws Exception {
        String fileName = SecretStorageUtils.generateFullNewWalletFile(TestConfig.ENCRYPT_PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    @Test
    public void testGenerateNewWalletFile() throws Exception {
        String fileName = SecretStorageUtils.generateNewWalletFile(TestConfig.ENCRYPT_PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    @Test
    public void testGenerateLightNewWalletFile() throws Exception {
        String fileName = SecretStorageUtils.generateLightNewWalletFile(TestConfig.ENCRYPT_PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    private void testGeneratedNewWalletFile(String fileName) throws Exception {
        WalletFile walletFile = objectMapper.readValue(new File(tempDir, fileName), WalletFile.class);
        SecretStorageUtils.loadSigningKey(TestConfig.ENCRYPT_PASSWORD, new File(tempDir, fileName));
    }

    @Test
    public void testGenerateFullWalletFile() throws Exception {
        String fileName = SecretStorageUtils.generateWalletFile(TestConfig.ENCRYPT_PASSWORD, KEY_PAIR, tempDir, true);
        testGenerateWalletFile(fileName);
    }

    @Test
    public void testGenerateLightWalletFile() throws Exception {
        String fileName = SecretStorageUtils.generateWalletFile(TestConfig.ENCRYPT_PASSWORD, KEY_PAIR, tempDir, false);
        testGenerateWalletFile(fileName);
    }

    private void testGenerateWalletFile(String fileName) throws Exception {
        SigningKey signingKey =
                SecretStorageUtils.loadSigningKey(TestConfig.ENCRYPT_PASSWORD, new File(tempDir, fileName));
        assertThat(signingKey, equalTo(SIGNING_KEY));
    }


    @Test
    public void testGetDefaultKeyDirectory() {
        assertTrue(
                SecretStorageUtils.getDefaultKeyDirectory("Mac OS X")
                        .endsWith(
                                String.format(
                                        "%sLibrary%smxw", File.separator, File.separator)));
        assertTrue(
                SecretStorageUtils.getDefaultKeyDirectory("Windows")
                        .endsWith(String.format("%smxw", File.separator)));
        assertTrue(
                SecretStorageUtils.getDefaultKeyDirectory("Linux")
                        .endsWith(String.format("%s.mxw", File.separator)));
    }


    static File createTempDir() throws Exception {
        return Files.createTempDirectory(SecretStorageUtilsTest.class.getSimpleName() + "-testkeys")
                .toFile();
    }

}
