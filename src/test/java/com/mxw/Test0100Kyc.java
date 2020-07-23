package com.mxw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.crypto.Keys;
import com.mxw.crypto.SecretStorage;
import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.KycRevokeBuilder;
import com.mxw.protocol.request.messages.builder.KycWhiteListBuilder;
import com.mxw.protocol.response.PublicKey;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.protocol.response.kyc.*;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.utils.Address;
import com.mxw.utils.Base64s;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Optional;

public class Test0100Kyc {

    private HttpService httpService;
    private JsonRpcProvider jsonRpcProvider;

    private Wallet userWallet;
    private Wallet providerWallet;
    private Wallet issuerWallet;
    private Wallet middlewareWallet;

    private ObjectMapper objectMapper;

    @Before
    public void initialize() throws Exception{

        this.httpService =  new HttpService(TestConfig.HTTP_SERVICE_URL, true);
        this.jsonRpcProvider = new JsonRpcProvider(this.httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));

        this.objectMapper = ObjectMapperFactory.getObjectMapper(false);

        String providerMnemonic = "into demand chief rubber raw hospital unit tennis sentence fade flight cluster";
        String issuerMnemonic = "pill maple dutch predict bulk goddess nice left paper heart loan fresh";
        String middlewareMnemonic = "avocado trade bright wolf marble penalty mimic curve funny name certain visa";

        this.providerWallet = Wallet.fromMnemonic(providerMnemonic, Optional.empty());
        this.issuerWallet = Wallet.fromMnemonic(issuerMnemonic, Optional.empty());
        this.middlewareWallet = Wallet.fromMnemonic(middlewareMnemonic, Optional.empty());

        this.providerWallet.connect(this.jsonRpcProvider);
        this.issuerWallet.connect(this.jsonRpcProvider);
        this.middlewareWallet.connect(this.jsonRpcProvider);

        userWallet = Wallet.createNewWallet();
        userWallet.connect(this.jsonRpcProvider);
    }

    @Test
    public void testGenerateKycAddress() throws Exception {
        String seed =Address.getChecksum("0xb3ad4dE471f47D5226C667e201f2125ac3FA0258e3944dBfC86550FCD0776f5e");
        KycAddress kycAddress = new KycAddress("MY", "NIC", "123456", 20200101, 19800101, seed.toLowerCase());
        String payload = this.objectMapper.writeValueAsString(kycAddress);
        String addressHex = Keys.getKeyAddressHex(payload);
        String computedKycAddress = Keys.computeKycAddress(addressHex,Constants.kycAddressPrefix);
        Assert.assertEquals(computedKycAddress,"kyc1v6kf4scf6pctpqxsqua5wwldn7lqh2cxc0vs924a3aywgf80z3ests6dc2");
    }

    @Test
    public void testKyc() throws Exception {

        System.out.println("Begin whitelist ... ");

        String country = "MY";
        String idType = "NIC";
        String id = userWallet.getAddress();
        int idExpiry = 20200101;
        int dob = 19800101;
        byte[] seed = SecretStorage.generateRandomBytes(32);

        BigInteger nonce = this.jsonRpcProvider.getTransactionCount(userWallet.getAddress());

        KycAddress kycAddress = new KycAddress(country, idType, id, idExpiry, dob, Address.getHash(seed).toLowerCase());
        String kycAddressHash = Keys.getKeyAddressHex(this.objectMapper.writeValueAsString(kycAddress));
        String computedKycAddress = Keys.computeKycAddress(kycAddressHash,Constants.kycAddressPrefix);
        KycWhitelistModel kycWhitelistModel = new KycWhitelistModel(userWallet.getAddress(), computedKycAddress, nonce);
        PublicKey kycPubKey = new PublicKey("tendermint/" + userWallet.getPublicKeyType(), Base64s.base16to64(userWallet.getCompressedPublicKey()));
        KycWhitelistPayload kycWhitelistPayload = new KycWhitelistPayload(kycWhitelistModel, kycPubKey, userWallet.getSignature(kycWhitelistModel).getSignature());

        KycData kycData = new KycData(kycWhitelistPayload);
        kycData.getSignatureList().add(this.providerWallet.getSignature(kycWhitelistPayload));
        kycData.getSignatureList().add(this.issuerWallet.getSignature(kycWhitelistPayload));

        KycWhitelist kycWhitelist = new KycWhitelist(kycData, middlewareWallet.getAddress());

        TransactionRequest request = this.jsonRpcProvider.getTransactionRequest("kyc","kyc-whitelist", new KycWhiteListBuilder(kycWhitelist));
        request.setChainId(this.jsonRpcProvider.getNetwork().getChainId());

        this.middlewareWallet.sign(request);
        TransactionResponse response = this.middlewareWallet.sendTransaction(request);

        System.out.println("Wait for transaction confirm ... ");
        Optional<TransactionReceipt> receipt = waitForTransaction(response.getHash(), 10, 15000);
        Assert.assertTrue(receipt.isPresent());

        Assert.assertEquals(response.getHash(), receipt.get().getHash());
        System.out.println("Begin revoke ... ");

        nonce = this.jsonRpcProvider.getTransactionCount(userWallet.getAddress());

        KycRevokeModel kycRevokeModel = new KycRevokeModel(providerWallet.getAddress(), nonce, userWallet.getAddress());
        kycPubKey = new PublicKey("tendermint/" + providerWallet.getPublicKeyType(), Base64s.base16to64(providerWallet.getCompressedPublicKey()));
        KycRevokePayload kycRevokePayload = new KycRevokePayload(kycRevokeModel, kycPubKey, providerWallet.getSignature(kycRevokeModel).getSignature());
        KycRevoke kycRevoke = new KycRevoke(middlewareWallet.getAddress(), kycRevokePayload, issuerWallet.getSignature(kycRevokePayload));

        request = this.jsonRpcProvider.getTransactionRequest("kyc","kyc-revokeWhitelist", new KycRevokeBuilder(kycRevoke));
        request.setChainId(this.jsonRpcProvider.getNetwork().getChainId());

        this.middlewareWallet.sign(request);
        this.middlewareWallet.sendTransaction(request);

        System.out.println("Done revoke ... ");
    }

    private Optional<TransactionReceipt> waitForTransaction(String hash, int attempt, int sleepDuration) throws InterruptedException {
        Optional<TransactionReceipt> transactionReceipt = getTransactionReceipt(hash);
            for(int i=0; i < attempt;i ++) {
                if(!transactionReceipt.isPresent()){
                    Thread.sleep(sleepDuration);
                    transactionReceipt = getTransactionReceipt(hash);
                }else {
                    break;
                }
            }
            return transactionReceipt;
    }

    private Optional<TransactionReceipt> getTransactionReceipt(String hash) {
        try{
            return Optional.of(this.jsonRpcProvider.getTransactionReceipt(hash, Object.class));
        }catch (TransactionException ex) {
            return Optional.empty();
        }
    }
}