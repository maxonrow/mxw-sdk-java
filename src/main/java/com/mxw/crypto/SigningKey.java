package com.mxw.crypto;

import com.mxw.utils.Numeric;

import java.util.Objects;

/**
   TODO: add verify computeSharedSecret
 **/
public class SigningKey {

    private String privateKey;

    private String publicKey;

    private String publicKeyType;

    private String compressedPublicKey;

    private String address;

    private String hexAddress;

    private String mnemonic;

    private String path;

    private ECKeyPair keyPair;


    public SigningKey(String privateKey) {
        this(Numeric.hexStringToByteArray(privateKey));
    }

    public SigningKey(byte[] privateKey) {
        this(ECKeyPair.create(privateKey));
        if(privateKey.length != 32) {
            throw  new IllegalArgumentException("Invalid private key");
        }
    }


    public SigningKey(ECKeyPair keyPair) {
        this.keyPair = keyPair;
        this.privateKey = this.keyPair.getPrivateKey();
        this.publicKey = this.keyPair.getPublicKey();
        this.compressedPublicKey = this.keyPair.getCompressedPublicKey();
        this.publicKeyType = this.keyPair.getPublicKeyType();
        this.address = Keys.computeAddress(this.keyPair.getCompressedPublicKey());
        this.hexAddress = Keys.computeHexAddress(this.address);
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKeyType() {
        return publicKeyType;
    }

    public void setPublicKeyType(String publicKeyType) {
        this.publicKeyType = publicKeyType;
    }

    public String getCompressedPublicKey() {
        return compressedPublicKey;
    }

    public void setCompressedPublicKey(String compressedPublicKey) {
        this.compressedPublicKey = compressedPublicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHexAddress() {
        return hexAddress;
    }

    public void setHexAddress(String hexAddress) {
        this.hexAddress = hexAddress;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ECKeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(ECKeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public ECDSASignature signDegest(byte[] digest) {
        return this.keyPair.sign(digest);
    }

    @Override
    public boolean equals(Object o) {
        if(this==o)
            return true;

        if(o==null || getClass() != o.getClass())
            return false;

        SigningKey thatKey = (SigningKey) o;

        if(!Objects.equals(keyPair, thatKey.keyPair)) {
            return false;
        }

        return address.equals(thatKey.address);
    }

    @Override
    public int hashCode() {
        int result = keyPair != null ? keyPair.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
