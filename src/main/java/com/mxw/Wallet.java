package com.mxw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.crypto.*;
import com.mxw.exceptions.CipherException;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.response.Signature;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.Provider;
import com.mxw.tx.DefaultTransactionManager;
import com.mxw.tx.TransactionManager;
import com.mxw.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static com.mxw.utils.Assertions.verifyPrecondition;


public class Wallet implements Signer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String MISSING_PROVIDER = "missing provider";

    private Provider provider;
    private SigningKey signingKey;

    private BigInteger accountNumber;

    private TransactionManager transactionManager;

    private ObjectMapper objectMapper;

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
        this.objectMapper = ObjectMapperFactory.getObjectMapper();
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
        return Numeric.toHexString(Sign.joinSignature(Sign.signMessage(message, this.signingKey.getKeyPair(), needToHash)));
    }

    @Override
    public String sign(TransactionRequest request) {
        return this.transactionManager.signAndSerialize(request);
    }

    @Override
    public TransactionResponse sendTransaction(TransactionValueBuilder builder) {
        return this.transactionManager.sendTransaction(builder);
    }

    @Override
    public TransactionResponse sendTransaction(TransactionRequest request) {
        return this.transactionManager.sendTransaction(request);
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
        if(this.transactionManager==null)
            this.transactionManager = new DefaultTransactionManager(this.provider, this.signingKey);
        else
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

    public SigningKey getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(SigningKey signingKey) {
        this.signingKey = signingKey;
    }

    public WalletFile EncryptWallet(String password) throws CipherException {
        return SecretStorage.createEncryptedWallet(password, this.signingKey);
    }

    public String EncryptWalletJson(String password) throws CipherException, JsonProcessingException {
        WalletFile walletFile = EncryptWallet(password);
        return objectMapper.writeValueAsString(walletFile);
    }

    public static Wallet createNewWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair keyPair = createRandomKeyPair();
        return new Wallet(keyPair.getPrivateKey());
    }

    public static Wallet fromMnemonic(String mnemonic) {
        return fromMnemonic(mnemonic, Optional.empty());
    }

    public static Wallet fromMnemonic(String mnemonic, Optional<int[]> path) {
        return new Wallet(SigningKey.fromMnemonic(mnemonic, path));
    }

    public static Wallet fromEncryptedJson(String json, String password) throws CipherException {
        WalletFile walletFile = SecretStorageUtils.getWalletFileFromJson(json);
        SigningKey signingKey = SecretStorage.decryptToSignKey(password, walletFile);
        return new Wallet(signingKey);
    }

    private static ECKeyPair createRandomKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Keys.createEcKeyPair();
    }

    public Signature getSignature(Object object) throws Exception{
        return this.transactionManager.sign(object);
    }

    public String computeSharedSecret(String otherPublicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException {
        return this.signingKey.computeSharedSecret(otherPublicKey);
    }
}

