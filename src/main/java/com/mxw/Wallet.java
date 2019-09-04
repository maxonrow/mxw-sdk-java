package com.mxw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.crypto.*;
import com.mxw.exceptions.CipherException;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.*;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.request.BlockTagName;
import com.mxw.providers.Provider;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.tx.DefaultTransactionManager;
import com.mxw.tx.TransactionManager;
import com.mxw.utils.Base64s;
import com.mxw.utils.Numeric;
import com.mxw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Optional;

import static com.mxw.utils.Assertions.verifyPrecondition;


public class Wallet implements Signer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String MISSING_PROVIDER = "missing provider";

    private Provider provider;
    private SigningKey signingKey;

    private BigInteger accountNumber;

    private TransactionManager transactionManager;

    public Wallet(ECKeyPair keyPair) {
        this(keyPair.getPrivateKey());
    }

    public Wallet(ECKeyPair keyPair, Provider provider){
        this(keyPair.getPrivateKey(), provider);
    }

    public Wallet(String privateKey) {
        this(new SigningKey(privateKey));
    }

    public Wallet(String privateKey, Provider provider) {
        this(new SigningKey(privateKey), provider);
    }

    public Wallet(byte[] privateKey) {
        this(new SigningKey(privateKey));
    }

    public Wallet(byte[] privateKey, Provider provider) {
        this(new SigningKey(privateKey), provider);
    }

    public Wallet(SigningKey privateKey) {
        this(privateKey, null);
    }

    public Wallet(SigningKey privateKey, Provider provider) {
        this(privateKey, provider, new DefaultTransactionManager(provider, privateKey));
    }

    public Wallet(SigningKey privateKey, Provider provider, TransactionManager transactionManager) {
        this.signingKey = privateKey;
        this.provider = provider;
        this.transactionManager = transactionManager;
    }

    @Override
    public Provider getProvider() {
        return this.provider;
    }

    @Override
    public String getAddress() {
        return this.signingKey.getAddress();
    }

    @Override
    public String getHexAddress() {
        return this.signingKey.getHexAddress();
    }

    @Override
    public String getPublicKeyType() {
        return this.signingKey.getPublicKeyType();
    }

    @Override
    public String getCompressedPublicKey() {
        return this.signingKey.getCompressedPublicKey();
    }

    public String getPublicKey() {
        return this.signingKey.getCompressedPublicKey();
    }

    public String getPrivateKey() {
        return this.signingKey.getPrivateKey();
    }

    @Override
    public String signMessage(byte[] message, boolean needToHash) {
        return new String(Sign.joinSignature(Sign.signMessage(message, this.signingKey.getKeyPair(), needToHash)));
    }

    @Override
    public String sign(TransactionRequest request) {
        return this.transactionManager.signAndSerialize(request);
    }

    @Override
    public TransactionResponse sendTransaction(TransactionValueBuilder builder) {
        return this.transactionManager.sendTransaction(builder);
    }


    public BigInteger getTransactionCount(BlockTag blockTag) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        return this.provider.getTransactionCount(this.getAddress(), blockTag);
    }

    public BigInteger getAccountNumber() {
        return this.getAccountNumber(null);
    }

    public BigInteger getAccountNumber(BlockTag blockTag) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        if(this.accountNumber==null) {
            this.accountNumber = this.provider.getAccountNumber(this.getAddress(), blockTag);
        }
        return this.accountNumber;
    }

    public TransactionResponse transfer(String addressOrName, BigInteger value, String memo) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        String toAddress = this.provider.resolveName(addressOrName);
        return this.sendTransaction(new BankSendBuilder(this.getAddress(),toAddress, value, memo));
    }

    public Wallet connect(Provider provider) {
        this.provider = provider;
        this.transactionManager.setProvider(this.provider);
        return this;
    }

    public BigInteger getBalance() {
        return getBalance(null);
    }

    public BigInteger getBalance(BlockTag blockTag) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        return this.provider.getBalance(this.getAddress(), blockTag);
    }

    public boolean isWhitelisted() {
        return isWhitelisted(null);
    }

    public boolean isWhitelisted(BlockTag blockTag) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        return this.provider.isWhiteListed(this.getAddress(), blockTag);
    }

    public String getKycAddress() {
        return getKycAddress(null);
    }

    public String getKycAddress(BlockTag blockTag) {
        verifyPrecondition(this.provider!=null, MISSING_PROVIDER);
        return this.provider.getKycAddress(this.getAddress(), blockTag);
    }

    public static Wallet createNewWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair keyPair = createRandomKeyPair();
        return new Wallet(keyPair.getPrivateKey());
    }

    public static Wallet fromMnemonic(String mnemonic, Optional<int[]> path) {
        if(!path.isPresent())
            path = Optional.of(Constants.DefaultHDPath);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44KeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path.get());
        return new Wallet(bip44KeyPair);
    }

    public static Wallet fromEncryptedJson(String json, String password) throws CipherException {
        WalletFile walletFile = SecretStorageUtils.getWalletFileFromJson(json);
        ECKeyPair keyPair = SecretStorage.decrypt(password, walletFile);
        return new Wallet(keyPair);
    }

    private static ECKeyPair createRandomKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Keys.createEcKeyPair();
    }

}

