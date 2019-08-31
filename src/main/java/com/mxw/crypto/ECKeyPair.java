package com.mxw.crypto;

import com.mxw.utils.Numeric;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Objects;

/**
 * Elliptic Curve SECP-256k1 generated key pair.
 *
 * implementation and adapted from <a href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/ECKeyPair.java">Web3j</a>
 */
public class ECKeyPair {

    private BigInteger privateKey;

    private BigInteger publicKey;

    private String publicKeyType;

    private BigInteger compressedPublicKey;

    private byte[] publicKeyBytes;

    public ECKeyPair(byte[] privateKey) {
        this(Numeric.toBigInt(privateKey));
    }

    public ECKeyPair(String privateKey) {
        this(Numeric.toBigInt(privateKey));
    }

    public ECKeyPair(BigInteger privateKey) {
        this(privateKey, Sign.publicKeyFromPrivate(privateKey));
    }

    public ECKeyPair(BigInteger privateKey, BigInteger publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.publicKeyType = "PubKeySecp256k1";
        this.compressedPublicKey = Sign.publicKeyFromPrivate(privateKey, true);
        this.publicKeyBytes = Numeric.hexStringToByteArray(this.getPublicKey());
    }

    /**
     * Sign a hash with the private key of this key pair.
     *
     * @param transactionHash the hash to sign
     * @return An {@link ECDSASignature} of the hash
     */
    public ECDSASignature sign(byte[] transactionHash) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, Sign.CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(transactionHash);

        return new ECDSASignature(components[0], components[1]).toCanonicalised();
    }

    public String getPrivateKey() {
        return Numeric.toHexStringWithPrefix(this.privateKey);
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = Numeric.toBigInt(privateKey);
    }

    public String getPublicKey() {
        return Numeric.toHexStringZeroPadded(this.publicKey,Keys.PUBLIC_KEY_SIZE, true);
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = Numeric.toBigInt(publicKey);
    }

    public String getPublicKeyType() {
        return publicKeyType;
    }

    public void setPublicKeyType(String publicKeyType) {
        this.publicKeyType = publicKeyType;
    }

    public String getCompressedPublicKey() {
        return Numeric.toHexStringZeroPadded(this.compressedPublicKey,Keys.PUBLIC_KEY_SIZE_COMPRESSED_PADDED, true);
    }

    public void setCompressedPublicKey(String compressedPublicKey) {
        this.compressedPublicKey = Numeric.toBigInt(compressedPublicKey);
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public void setPublicKeyBytes(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ECKeyPair ecKeyPair = (ECKeyPair) o;

        if (!Objects.equals(privateKey, ecKeyPair.privateKey)) {
            return false;
        }
        return Objects.equals(publicKey, ecKeyPair.publicKey);
    }

    @Override
    public int hashCode() {
        int result = privateKey != null ? privateKey.hashCode() : 0;
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }


    public static ECKeyPair create(KeyPair keyPair) {
        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

        BigInteger privateKeyValue = privateKey.getD();

        // Ethereum does not use encoded public keys like bitcoin - see
        // https://en.bitcoin.it/wiki/Elliptic_Curve_Digital_Signature_Algorithm for details
        // Additionally, as the first bit is a constant prefix (0x04) we ignore this value
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        BigInteger publicKeyValue =
                new BigInteger(1, Arrays.copyOfRange(publicKeyBytes, 0, publicKeyBytes.length));

        return new ECKeyPair(privateKeyValue, publicKeyValue);
    }

    public static ECKeyPair create(BigInteger privateKey) {
        return new ECKeyPair(privateKey, Sign.publicKeyFromPrivate(privateKey));
    }

    public static ECKeyPair create(String privateKey) {
        return create(Numeric.toBigInt(privateKey));
    }

    public static ECKeyPair create(byte[] privateKey) {
        return create(Numeric.toBigInt(privateKey));
    }

}
