package com.mxw.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.exceptions.InvalidResponseException;
import com.mxw.exceptions.JsonRpcClientException;
import com.mxw.networks.Network;
import com.mxw.networks.Networks;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.request.BlockTagName;
import com.mxw.protocol.response.*;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.mxw.utils.Assertions.verifyPrecondition;

public abstract class BaseProvider extends AbstractProvider {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Network network;
    private ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    public BaseProvider() {
        this(Networks.HOMESTEAD.getNetwork());
    }

    public BaseProvider(Network network) {
        Network knownNetwork = Network.getNetwork(network==null? Networks.HOMESTEAD.getNetwork() : network);
        if(knownNetwork==null){
            throw new IllegalArgumentException("Invalid network");
        }
        this.network = knownNetwork;
    }

    @Override
    public Network getNetwork() {
        return this.network;
    }

    @Override
    public BigInteger getBlockNumber() {
        Status status = this.getStatus();
        BigInteger blockNumber = null;
        if(status!=null && status.getSyncInfo()!=null && status.getSyncInfo().getLatestBlockHeight()!=null)
            blockNumber =  status.getSyncInfo().getLatestBlockHeight();

        if(blockNumber==null)
            throw new InvalidResponseException("invalid response - getBlockNumber");

        return blockNumber;
    }

    @Override
    public TransactionRequest getTransactionRequest(String route, String transactionType, TransactionValueBuilder builder) {
        verifyPrecondition(builder!=null, "missing transaction field");
        TransactionRequest request = new TransactionRequest();
        String moduleName = route + "/" + transactionType;
        switch (moduleName.toLowerCase()){
            case "bank/bank-send":
            case "kyc/kyc-whitelist":
            case "kyc/kyc-revokeWhitelist":
            case "nameservice/nameservice-setAliasStatus":
            case "nameservice/nameservice-createAlias":
                request.setType("cosmos-sdk/StdTx");
                break;
            default:
                throw new UnsupportedOperationException("operation not implemented: " +moduleName);
        }
        request.setValue(builder.build());
        return request;
    }

    @Override
    public TransactionFee getTransactionFee(String route, String transactionType, TransactionRequest request) {
        try {
            TransactionFee response = this.perform("getTransactionFee", TransactionFee.class, Base64s.encode(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)));
            return response;
        }catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("failed to serialize TransactionRequest ");
        }
    }

    @Override
    public TransactionFeeSetting getTransactionFeeSetting(String transactionType) {
       throw new UnsupportedOperationException();
    }

    @Override
    public Status getStatus() {
        return this.perform("status", Status.class);
    }

    @Override
    public TransactionFee getTokenTransactionFee(String symbol, String transactionType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenState getTokenState(String symbol, BlockTag blockTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenList getTokenList(BlockTag blockTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenAccountState getTokenAccountState(String symbol, String address, BlockTag blockTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountState getAccountState(String addressOrName) {
        return this.getAccountState(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public AccountState getAccountState(String addressOrName, BlockTag blockTag) {
       String address = this.resolveName(addressOrName, blockTag);
       String block = checkBlockTag(blockTag);
        return this.perform("getAccountState", AccountState.class, address);

    }

    @Override
    public BigInteger getAccountNumber(String addressOrName) {
        return this.getAccountNumber(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public BigInteger getAccountNumber(String addressOrName, BlockTag blockTag) {
        if(blockTag==null) {
            blockTag = BlockTagName.LATEST;
        }
        AccountState accountState = this.getAccountState(addressOrName, blockTag);
        if(accountState!=null && accountState.getValue()!=null && accountState.getValue().getAccountNumber()!=null)
            return accountState.getValue().getAccountNumber();

        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getBalance(String addressOrName) {
        return this.getBalance(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public BigInteger getBalance(String addressOrName, BlockTag blockTag) {
        AccountState accountState = this.getAccountState(addressOrName, blockTag);
        if(accountState!=null && accountState.getValue()!=null && accountState.getValue().getCoins()!=null){
            List<Coin> coins = accountState.getValue().getCoins();
            if(coins.size() > 0) {
                if(coins.get(0).getAmount()!=null)
                    return coins.get(0).getAmount();
            }
        }
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getTransactionCount(String addressOrName) {
        return this.getTransactionCount(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public BigInteger getTransactionCount(String addressOrName, BlockTag blockTag) {
        AccountState accountState = this.getAccountState(addressOrName, blockTag);
        if(accountState!=null && accountState.getValue()!=null && accountState.getValue().getSequence()!=null)
            return accountState.getValue().getSequence();

        return BigInteger.ZERO;
    }

    @Override
    public TransactionResponse sendTransaction(String signedTransaction, boolean async){
        String method = async ? "sendTransactionAsync" : "sendTransaction";
        InnerResponse response = this.perform(method, InnerResponse.class, signedTransaction);
        if(response.getCode()== 0 && !Strings.isEmpty(response.getHash())) {
           return new TransactionResponse(Numeric.prependHexPrefix(response.getHash()).toLowerCase());
        }
        handleErrorResponse(response.getCode(), response.getLog());
        return null;
    }

    @Override
    public Block getBlock(long height) {
        return getBlock(new BlockTagNumber(height));
    }

    @Override
    public Block getBlock(BigInteger height) {
        return getBlock(new BlockTagNumber(height));
    }

    @Override
    public Block getBlock(BlockTag blockHashOrBlockTag) {
        try{
            String blockHash = blockHashOrBlockTag.getValue();
            if(Numeric.isValidHex(blockHash) && Bytes.getHashLength(blockHash) == 32){
                this.perform("getBlock", Block.class, blockHash);
            }
        }catch (Exception ex){

        }

        BigInteger blockNumber = BigInteger.valueOf(-128);
        String blockTag = checkBlockTag(blockHashOrBlockTag);
        if(Numeric.isValidHex(blockTag)){
            blockNumber = Numeric.decodeQuantity(blockTag);
        }

        if(blockNumber.compareTo(BigInteger.ZERO) == 0) {
            BigInteger latestBlock = this.getBlockNumber();
            return this.getBlock(new BlockTagNumber(latestBlock));
        }

        return this.perform("getBlock", Block.class, blockTag);
    }

    @Override
    public TransactionReceipt getTransaction(String transactionHash) {
        return this.getTransactionReceipt(transactionHash, Object.class);
    }

    @Override
    public <T> TransactionReceipt<T> getTransactionReceipt(String transactionHash, Class<T> receiptType) {
        transactionHash = Numeric.prependHexPrefix(transactionHash);
        String base64 = Base64s.encode(Numeric.hexStringToByteArray(transactionHash));
        JavaType type = this.objectMapper.getTypeFactory().constructParametricType(TransactionReceipt.class, receiptType);
        return this.perform("getTransactionReceipt", type, base64, null);
    }

    @Override
    public Boolean isWhiteListed(String addressOrName) {
        return this.isWhiteListed(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public Boolean isWhiteListed(String addressOrName, BlockTag blockTag) {
        String path = "/custom/kyc/is_whitelisted/" + addressOrName;
        TypeReference<AbciResponse<Boolean>> typeReference = new TypeReference<AbciResponse<Boolean>>(){};
        String tag = checkBlockTag(blockTag);
        AbciResponse<Boolean> response = this.perform("isWhitelisted", typeReference.getType(), path,"", tag, null);
        return response.getValue();
    }

    @Override
    public String getKycAddress(String addressOrName) {
        return this.getKycAddress(addressOrName, BlockTagName.LATEST);
    }

    @Override
    public String getKycAddress(String addressOrName, BlockTag blockTag) {
        String path = "/custom/kyc/get_kyc_address/" + addressOrName;
        TypeReference<AbciResponse<String>> typeReference = new TypeReference<AbciResponse<String>>(){};
        String tag = checkBlockTag(blockTag);
        AbciResponse<String> response = this.perform("getKycAddress", typeReference.getType(), path,"", tag, null);
        return response.getValue();
    }

    @Override
    public String resolveName(String name) {
        return this.resolveName(name, BlockTagName.LATEST);
    }

    @Override
    public String resolveName(String name, BlockTag blockTag) {
        try{
            return Address.getAddress(name);
        }catch (Exception ex){

        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String lookupAddress(String address) {
        return this.lookupAddress(address, BlockTagName.LATEST);
    }

    @Override
    public String lookupAddress(String address, BlockTag blockTag) {
       String path = "/custom/nameservice/whois/" + address;
        TypeReference<AbciResponse<String>> typeReference = new TypeReference<AbciResponse<String>>(){};
        String tag = checkBlockTag(blockTag);
        AbciResponse<String> response = this.perform("lookupAddress", typeReference.getType(), path,"", tag, null);
        return response.getValue();
    }

    @Override
    public String getAliasState(String address) {
        return this.getAliasState(address, BlockTagName.LATEST);
    }

    @Override
    public String getAliasState(String address, BlockTag blockTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionReceipt waitForTransaction(String transactionHash, BigInteger confirmation) {
        throw new UnsupportedOperationException();
    }

    protected abstract  <T> T perform(String method, Class<?> responseType, Object ... params);
    protected abstract  <T> T perform(String method, Type responseType, Object ... params);

    protected void handleErrorResponse(int code, String message){
        throw  new JsonRpcClientException(code, message);
    }

    private String checkBlockTag(BlockTag blockTag) {
        if(blockTag == null || blockTag.getValue().equals("earliest") || blockTag.getValue().equals("latest") || blockTag.getValue().equals("pending"))
            return "0";

        if(Numeric.isValidHex(blockTag.getValue())){
            return Numeric.toBigInt(blockTag.getValue()).toString();
        }

        try{
            return new BigInteger(blockTag.getValue()).toString();
        }catch (Exception ex){

        }
        throw new IllegalArgumentException("Invalid blockTag");
    }
}
