package com.mxw.crypto;

import com.mxw.Constants;
import com.mxw.utils.Address;
import com.mxw.utils.Numeric;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static com.mxw.crypto.SecureRandomUtils.secureRandom;

/**
 * Keys utils
 *
 * implementation and adapted from <a href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Keys.java">Web3j</a>
 */
public class Keys {

    static final int PRIVATE_KEY_SIZE = 32;
    static final int PUBLIC_KEY_SIZE_COMPRESSED = 64;
    static final int PUBLIC_KEY_SIZE_COMPRESSED_PADDED = PUBLIC_KEY_SIZE_COMPRESSED + 2;

    static final int PUBLIC_KEY_SIZE = 130;
    public static final int PRIVATE_KEY_LENGTH_IN_HEX = PRIVATE_KEY_SIZE << 1;

    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Create a keypair using SECP-256k1 curve.
     *
     * <p>Private keypairs are encoded using PKCS8
     *
     * <p>Private keys are encoded using X.509
     */
    static KeyPair createSecp256k1KeyPair()
            throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        return createSecp256k1KeyPair(secureRandom());
    }

    static KeyPair createSecp256k1KeyPair(SecureRandom random)
            throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        if (random != null) {
            keyPairGenerator.initialize(ecGenParameterSpec, random);
        } else {
            keyPairGenerator.initialize(ecGenParameterSpec);
        }
        return keyPairGenerator.generateKeyPair();
    }

    public static ECKeyPair createEcKeyPair()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        return createEcKeyPair(secureRandom());
    }

    public static ECKeyPair createEcKeyPair(SecureRandom random)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        KeyPair keyPair = createSecp256k1KeyPair(random);
        return ECKeyPair.create(keyPair);
    }


    public static String computePublicKey(String key) {
        return computePublicKey(key,true);
    }

    public static String computePublicKey(String key, boolean compressed) {
        return computePublicKey(Numeric.hexStringToByteArray(key), compressed);
    }

    public static String computePublicKey(byte[] bytes, boolean compressed) {
        if(bytes.length== 32) {
            ECKeyPair keyPair = new ECKeyPair(bytes);
            if(compressed)
                return keyPair.getCompressedPublicKey();

            return keyPair.getPublicKey();
        }else if(bytes.length == 33) {
            if(compressed)
                return Numeric.toHexString(bytes);

            ECPoint point = Sign.decompressKey(Numeric.toBigInt(bytes), true);
            byte[] encoded = point.getEncoded(false);
            return Numeric.toHexString(Arrays.copyOfRange(encoded, 0, encoded.length));
        }else if(bytes.length == 65) {
            if(!compressed)
                return Numeric.toHexString(bytes);
            ECPoint point = Sign.CURVE.getCurve().decodePoint(bytes);
            byte[] encoded = point.getEncoded(true);
            return Numeric.toHexString(Arrays.copyOfRange(encoded, 0, encoded.length));
        }
        throw new IllegalArgumentException("invalid public or private key");
    }

    public static String computeAddress(ECKeyPair keyPair) {
        return computeAddress(keyPair.getCompressedPublicKey());
    }

    public static String computeAddress(BigInteger publicKey) {
        return computeAddress(computePublicKey(Numeric.toHexStringWithPrefix(publicKey),true));
    }

    public static String computeAddress(String publicKey) {
        byte[] key = Numeric.hexStringToByteArray(publicKey);
        byte[] bytes = Hash.sha256hash160(key);
        return Address.getAddress(Bech32.encode(Constants.AddressPrefix, Bech32.toWords(bytes)));
    }

    public static String computeKycAddress(String hash, String prefix) {
        byte[] bytes = Numeric.hexStringToByteArray(hash);
        return  Address.getAddress(Bech32.encode(prefix, Bech32.toWords(bytes)));
    }

    public static String computeHexAddress(String address) {
        return Address.getAddress(Numeric.toHexString(Bech32.fromWords(Bech32.decode(address).data)));
    }

    public static String recoverPublicKey(byte[] digest, Sign.SignatureData signature) throws SignatureException {
        BigInteger key = Sign.signedMessageHashToKey(digest, signature);
        return computePublicKey(Numeric.toHexStringWithPrefix(key),true);
    }

    public static String recoverAddress(byte[] digest, Sign.SignatureData signature) throws SignatureException {
        return computeAddress(recoverPublicKey(digest, signature));
    }

    public static String verifyMessage(String message, Sign.SignatureData signature) throws SignatureException {
        return verifyMessage(message.getBytes(StandardCharsets.UTF_8), signature);
    }

    public static String verifyMessage(byte[] message, Sign.SignatureData signature) throws SignatureException {
        return recoverAddress(Hash.sha256(message),signature);
    }

    public static String getKeyAddressHex(String payload) throws Exception{
        byte[] hash = payload.getBytes(StandardCharsets.UTF_8);
        return Numeric.toHexString(Hash.sha256(hash));
    }

    public static PublicKey keyFromPublic(String publicKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = createBCKeyFactory();
        return keyFromPublic(factory, publicKey);
    }

    public static PublicKey keyFromPublic(KeyFactory factory, String publicKey) throws InvalidKeySpecException {
        String computedPublicKey = Keys.computePublicKey(publicKey, false);
        byte[] publicKeyBytes = Numeric.hexStringToByteArray(computedPublicKey);
        ECPoint point =  Sign.CURVE.getCurve().decodePoint(publicKeyBytes);
        ECNamedCurveParameterSpec params = new ECNamedCurveParameterSpec("secp256k1", Sign.CURVE_SPEC.getCurve(), Sign.CURVE_SPEC.getG(), Sign.CURVE_SPEC.getN());
        return factory.generatePublic(new ECPublicKeySpec(point, params));
    }

    public static PrivateKey keyFromPrivate(String privateKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = createBCKeyFactory();
        return keyFromPrivate(factory, privateKey);
    }

    public static PrivateKey keyFromPrivate(KeyFactory factory, String privateKey) throws InvalidKeySpecException {
        byte[] privateKeyBytes =  Numeric.hexStringToByteArray(privateKey);
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(1, privateKeyBytes), Sign.CURVE_SPEC);
        return factory.generatePrivate(ecPrivateKeySpec);
    }

    public static KeyFactory createBCKeyFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
        return  KeyFactory.getInstance("ECDSA", "BC");
    }

}
