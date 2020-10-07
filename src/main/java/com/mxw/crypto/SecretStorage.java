package com.mxw.crypto;

import com.mxw.Constants;
import com.mxw.exceptions.CipherException;
import com.mxw.utils.Numeric;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static com.mxw.crypto.SecureRandomUtils.secureRandom;


/**
 * Ethereum wallet file management. For reference, refer to <a
 * href="https://github.com/ethereum/wiki/wiki/Web3-Secret-Storage-Definition">Web3 Secret Storage
 * Definition</a> or the <a
 * href="https://github.com/ethereum/go-ethereum/blob/master/accounts/key_store_passphrase.go">Go
 * Ethereum client implementation</a>.
 *
 * <p><strong>Note:</strong> the Bouncy Castle Scrypt implementation {@link SCrypt}, fails to comply
 * with the following Ethereum reference <a
 * href="https://github.com/ethereum/wiki/wiki/Web3-Secret-Storage-Definition#scrypt">Scrypt test
 * vector</a>:
 *
 * <pre>{@code
 * // Only value of r that cost (as an int) could be exceeded for is 1
 * if (r == 1 && N_STANDARD > 65536)
 * {
 *     throw new IllegalArgumentException("Cost parameter N_STANDARD must be > 1 and < 65536.");
 * }
 * }</pre>
 *
 * implement from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/crypto/Wallet.java">web3j</a>
 */
public class SecretStorage {

    private static final int N_LIGHT = 1 << 12;
    private static final int P_LIGHT = 6;

    private static final int N_STANDARD = 1 << 17;
    private static final int P_STANDARD = 1;

    private static final int R = 8;
    private static final int DKLEN = 32;
    private static final int DKLEN_256 = 48;

    private static final int CURRENT_VERSION = 3;

    private static final String CIPHER = "aes-128-ctr";
    private static final String CIPHER_256 = "aes-256-ctr";
    static final String AES_128_CTR = "pbkdf2";
    static final String SCRYPT = "scrypt";

    public static WalletFile create(String password, ECKeyPair ecKeyPair, int n, int p)
            throws CipherException {

        return create(password, ecKeyPair, n, p, null, Constants.DefaultHDPath);
    }

    public static WalletFile create(String password, ECKeyPair ecKeyPair, int n, int p, String mnemonic, int[] hdPath)
            throws CipherException {
        String cipher = CIPHER_256;
        int dklen = getDKlen(cipher);
        byte[] salt = generateRandomBytes(32);
        // generate derivedKey with 80 length instead of DKLEN for mxw metadata encryption
        byte[] derivedKey = generateDerivedScryptKey(password.getBytes(UTF_8), salt, n, R, p, 80);
        byte[] encryptKey = getEncryptKey(cipher, derivedKey);
        byte[] iv = generateRandomBytes(16);
        byte[] privateKeyBytes =
                Numeric.toBytesPadded(Numeric.toBigInt(ecKeyPair.getPrivateKey()), Keys.PRIVATE_KEY_SIZE);

        byte[] cipherText =
                performCipherOperation(Cipher.ENCRYPT_MODE, iv, encryptKey, privateKeyBytes);

        byte[] mac = getMac(cipher, derivedKey, cipherText);

        MnemonicOption mnemonicOption = null;
        if(mnemonic!=null && !mnemonic.equals("")){
            byte[] mnemonicKey = getMnemonicKey(cipher, derivedKey);
            byte[] entropy = MnemonicUtils.generateEntropy(mnemonic);
            mnemonicOption = new MnemonicOption(mnemonic, entropy, mnemonicKey, hdPath);
        }

        return createWalletFile(ecKeyPair, cipherText, iv, salt, mac, n, p, mnemonicOption);
    }

    public static WalletFile createStandard(String password, ECKeyPair ecKeyPair)
            throws CipherException {
        return create(password, ecKeyPair, N_STANDARD, P_STANDARD);
    }

    public static WalletFile createLight(String password, ECKeyPair ecKeyPair)
            throws CipherException {
        return create(password, ecKeyPair, N_LIGHT, P_LIGHT);
    }

    public static WalletFile createEncryptedWallet(String password, ECKeyPair ecKeyPair, String mnemonic, int[] hdPath) throws CipherException {
        if(mnemonic==null || mnemonic.equals("")){
            return createStandard(password, ecKeyPair);
        }
        return create(password, ecKeyPair, N_STANDARD, P_STANDARD, mnemonic, hdPath);
    }

    public static WalletFile createEncryptedWallet(String password, SigningKey signingKey) throws CipherException {
       return createEncryptedWallet(password, signingKey.getKeyPair(), signingKey.getMnemonic(), signingKey.getPath());
    }

    private static WalletFile createWalletFile(
            ECKeyPair ecKeyPair,
            byte[] cipherText,
            byte[] iv,
            byte[] salt,
            byte[] mac,
            int n,
            int p) throws CipherException {
        return createWalletFile(ecKeyPair, cipherText, iv, salt, mac, n, p, null);
    }

    private static WalletFile createWalletFile(
            ECKeyPair ecKeyPair,
            byte[] cipherText,
            byte[] iv,
            byte[] salt,
            byte[] mac,
            int n,
            int p, MnemonicOption mnemonicOption) throws CipherException {

        WalletFile walletFile = new WalletFile();
        walletFile.setAddress(Keys.computeAddress(ecKeyPair));

        WalletFile.Crypto crypto = new WalletFile.Crypto();
        crypto.setCipher(CIPHER_256);
        crypto.setCiphertext(Numeric.toHexStringNoPrefix(cipherText));

        WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
        cipherParams.setIv(Numeric.toHexStringNoPrefix(iv));
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(SCRYPT);
        WalletFile.ScryptKdfParams kdfParams = new WalletFile.ScryptKdfParams();
        kdfParams.setDklen(getDKlen(crypto.getCipher()));
        kdfParams.setN(n);
        kdfParams.setP(p);
        kdfParams.setR(R);
        kdfParams.setSalt(Numeric.toHexStringNoPrefix(salt));
        crypto.setKdfparams(kdfParams);

        crypto.setMac(Numeric.toHexStringNoPrefix(mac));
        walletFile.setCrypto(crypto);
        walletFile.setId(UUID.randomUUID().toString());
        walletFile.setVersion(CURRENT_VERSION);

        if(mnemonicOption!=null) {
            walletFile.setMxw(generateMxwProperties(walletFile, mnemonicOption));
        }

        return walletFile;
    }

    private static byte[] generateDerivedScryptKey(
            byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        // Java 8 supports this, but you have to convert the password to a character array, see
        // http://stackoverflow.com/a/27928435/3211687

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    private static byte[] performCipherOperation(
            int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | InvalidKeyException
                | BadPaddingException
                | IllegalBlockSizeException e) {
            throw new CipherException("Error performing cipher operation", e);
        }
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText, int srcPos) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, srcPos, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha256(result);
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        return generateMac(derivedKey, cipherText, 16);
    }

    public static ECKeyPair decrypt(String password, WalletFile walletFile) throws CipherException {

        validate(walletFile);

        WalletFile.Crypto crypto = walletFile.getCrypto();
        String cipher = crypto.getCipher();
        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        WalletFile.KdfParams kdfParams = crypto.getKdfparams();
        if (kdfParams instanceof WalletFile.ScryptKdfParams) {
            WalletFile.ScryptKdfParams scryptKdfParams =
                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            derivedKey = generateDerivedScryptKey(password.getBytes(UTF_8), salt, n, r, p, 80);
        } else if (kdfParams instanceof WalletFile.Aes128CtrKdfParams) {
            WalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (WalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(UTF_8), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }
        byte[] derivedMac = getMac(cipher, derivedKey, cipherText);
        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }

        return ECKeyPair.create(getPrivateKey(cipher, derivedKey, iv, cipherText));
    }

    public static SigningKey decryptToSignKey(String password, WalletFile walletFile) throws CipherException {
        validate(walletFile);

        WalletFile.Crypto crypto = walletFile.getCrypto();
        String cipher = crypto.getCipher();
        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        WalletFile.KdfParams kdfParams = crypto.getKdfparams();
        if (kdfParams instanceof WalletFile.ScryptKdfParams) {
            WalletFile.ScryptKdfParams scryptKdfParams =
                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            // generate derivedKey with 80 length instead of DKLEN for mxw metadata decryption
            derivedKey = generateDerivedScryptKey(password.getBytes(UTF_8), salt, n, r, p, 80);
        } else if (kdfParams instanceof WalletFile.Aes128CtrKdfParams) {
            WalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (WalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(UTF_8), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }
        byte[] derivedMac = getMac(cipher, derivedKey, cipherText);
        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }
        String privateKey = Numeric.toHexString(getPrivateKey(cipher, derivedKey, iv, cipherText));
        SigningKey signingKey;
        if(walletFile.getMxw()!=null) {
            WalletFile.MxwMetaData mxwMetaData = walletFile.getMxw();
            byte[] mnemonicCipherText = Numeric.hexStringToByteArray(mxwMetaData.getMnemonicCiphertext());
            byte[] mnemonicCounter =  Numeric.hexStringToByteArray(mxwMetaData.getMnemonicCounter());
            String path = mxwMetaData.getPath();
            byte[] mnemonicKey = getMnemonicKey(cipher, derivedKey);
            byte[] entropy = performCipherOperation(Cipher.DECRYPT_MODE, mnemonicCounter, mnemonicKey, mnemonicCipherText);
            MnemonicUtils.validateEntropy(entropy);
            String mnemonic = MnemonicUtils.generateMnemonic(entropy);
            Optional<int[]> hdPath = Optional.ofNullable(HDPathUtils.fromStringPath(path));
            signingKey = SigningKey.fromMnemonic(mnemonic, hdPath);
            if(!privateKey.equalsIgnoreCase(signingKey.getPrivateKey())){
                throw new CipherException("mnemonic mismatch ");
            }
        }else {
            signingKey = new SigningKey(privateKey);
        }
        return signingKey;
    }

    static void validate(WalletFile walletFile) throws CipherException {
        WalletFile.Crypto crypto = walletFile.getCrypto();

        if (walletFile.getVersion() != CURRENT_VERSION) {
            throw new CipherException("Wallet version is not supported");
        }

        if (!crypto.getCipher().equals(CIPHER) && !crypto.getCipher().equals(CIPHER_256)) {
            throw new CipherException("Wallet cipher is not supported");
        }

        if (!crypto.getKdf().equals(AES_128_CTR) && !crypto.getKdf().equals(SCRYPT)) {
            throw new CipherException("KDF type is not supported");
        }
    }

    public static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        secureRandom().nextBytes(bytes);
        return bytes;
    }

    private static byte[] getMac(String cipher, byte[] derivedKey, byte[] cipherText) {
        if(cipher.equals(CIPHER_256)) {
            return generateMac(derivedKey, cipherText, 32);
        }else {
          return generateMac(derivedKey, cipherText);
        }
    }

    private static byte[] getEncryptKey(String cipher, byte[] derivedKey) {
        byte[] encryptKey;
        if(cipher.equals(CIPHER_256)) {
            encryptKey = Arrays.copyOfRange(derivedKey, 0, 32);
        }else {
            encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        }
        return encryptKey;
    }

    private static byte[] getPrivateKey(String cipher, byte[] derivedKey, byte[] iv, byte[] cipherText) throws CipherException {
        byte[] encryptKey = getEncryptKey(cipher, derivedKey);
        return performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
    }

    private static int getDKlen(String cipher) {
        if(cipher.equals(CIPHER_256)) {
            return DKLEN_256;
        }
        return DKLEN;
    }

    private static byte[] getMnemonicKey(String cipher, byte[] derivedKey) {
        byte[] mnemonicKey;
        if(cipher.equals(CIPHER_256)) {
            mnemonicKey = Arrays.copyOfRange(derivedKey, 48, 80);
        }else {
            mnemonicKey = Arrays.copyOfRange(derivedKey, 32, 64);
        }
        return mnemonicKey;
    }

    private static WalletFile.MxwMetaData generateMxwProperties(WalletFile walletFile, MnemonicOption mnemonicOption) throws CipherException {
        byte[] mnemonicIv = generateRandomBytes(16);
        byte[] mnemonicCiphertext = performCipherOperation(Cipher.ENCRYPT_MODE, mnemonicIv, mnemonicOption.getMnemonicKey(), mnemonicOption.entropy);
        WalletFile.MxwMetaData metaData = new WalletFile.MxwMetaData(getWalletFileName(walletFile),Numeric.toHexStringNoPrefix(mnemonicIv), Numeric.toHexStringNoPrefix(mnemonicCiphertext));
        metaData.setPath(HDPathUtils.toStringPath(mnemonicOption.hdPath));
        return metaData;
    }

    private static String getWalletFileName(WalletFile walletFile) {
        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        return now.format(format) + walletFile.getAddress();
    }

    private static class MnemonicOption {

        public MnemonicOption() {

        }

        public MnemonicOption(String mnemonic, byte[] entropy, byte[] mnemonicKey, int[] hdPath){
            this.mnemonic = mnemonic;
            this.entropy = entropy;
            this.mnemonicKey = mnemonicKey;
            this.hdPath = hdPath;
        }

        private String mnemonic;

        private byte[] entropy;

        private byte[] mnemonicKey;

        private int[] hdPath;

        public String getMnemonic() {
            return mnemonic;
        }

        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }

        public byte[] getEntropy() {
            return entropy;
        }

        public void setEntropy(byte[] entropy) {
            this.entropy = entropy;
        }

        public byte[] getMnemonicKey() {
            return mnemonicKey;
        }

        public void setMnemonicKey(byte[] mnemonicKey) {
            this.mnemonicKey = mnemonicKey;
        }

        public int[] getHdPath() {
            return hdPath;
        }

        public void setHdPath(int[] hdPath) {
            this.hdPath = hdPath;
        }
    }

}
