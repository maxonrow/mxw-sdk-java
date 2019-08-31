package com.mxw.crypto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.exceptions.CipherException;
import com.mxw.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.mxw.crypto.Hash.sha256;
import static com.mxw.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static com.mxw.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;


/** Utility functions for working with Wallet files.
 *  implement from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/crypto/WalletUtils.java">web3j</a>
 * */
public class SecretStorageUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String generateFullNewWalletFile(String password, File destinationDirectory)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException, IOException {

        return generateNewWalletFile(password, destinationDirectory, true);
    }

    public static String generateLightNewWalletFile(String password, File destinationDirectory)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException, IOException {

        return generateNewWalletFile(password, destinationDirectory, false);
    }

    public static String generateNewWalletFile(String password, File destinationDirectory)
            throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException {
        return generateFullNewWalletFile(password, destinationDirectory);
    }

    public static String generateNewWalletFile(
            String password, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        return generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt);
    }

    public static String generateWalletFile(
            String password, ECKeyPair ecKeyPair, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException {

        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = SecretStorage.createStandard(password, ecKeyPair);
        } else {
            walletFile = SecretStorage.createLight(password, ecKeyPair);
        }

        String fileName = getWalletFileName(walletFile);
        File destination = new File(destinationDirectory, fileName);

        objectMapper.writeValue(destination, walletFile);

        return fileName;
    }

    /**
     * Generates a BIP-39 compatible Ethereum wallet. The private key for the wallet can be
     * calculated using following algorithm:
     *
     * <pre>
     *     Key = SHA-256(BIP_39_SEED(mnemonic, password))
     * </pre>
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param destinationDirectory The directory containing the wallet
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    public static Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    /**
     * Generates a BIP-39 compatible Ethereum wallet using a mnemonic passed as argument.
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param mnemonic The mnemonic that will be used to generate the seed
     * @param destinationDirectory The directory containing the wallet
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    public static Bip39Wallet generateBip39WalletFromMnemonic(
            String password, String mnemonic, File destinationDirectory)
            throws CipherException, IOException {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    public static SigningKey loadSigningKey(String password, String source)
            throws IOException, CipherException {
        return loadSigningKey(password, new File(source));
    }

    public static SigningKey loadSigningKey(String password, File source)
            throws IOException, CipherException {
        WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
        return new SigningKey(SecretStorage.decrypt(password, walletFile));
    }

    public static SigningKey loadBip39SigningKey(String password, String mnemonic) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        return new SigningKey(ECKeyPair.create(sha256(seed)));
    }

    private static String getWalletFileName(WalletFile walletFile) {
        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        return now.format(format) + walletFile.getAddress() + ".json";
    }

    public static String getDefaultKeyDirectory() {
        return getDefaultKeyDirectory(System.getProperty("os.name"));
    }

    static String getDefaultKeyDirectory(String osName1) {
        String osName = osName1.toLowerCase();

        if (osName.startsWith("mac")) {
            return String.format(
                    "%s%sLibrary%smxw",
                    System.getProperty("user.home"), File.separator, File.separator);
        } else if (osName.startsWith("win")) {
            return String.format("%s%smxw", System.getenv("APPDATA"), File.separator);
        } else {
            return String.format("%s%s.mxw", System.getProperty("user.home"), File.separator);
        }
    }

    public static WalletFile getWalletFileFromJson(String json) throws CipherException {
        try {
            WalletFile walletFile = objectMapper.readValue(json, WalletFile.class);
            SecretStorage.validate(walletFile);
            return walletFile;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid json wallet");
        }
    }

}
