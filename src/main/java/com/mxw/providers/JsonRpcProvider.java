package com.mxw.providers;

import com.mxw.exceptions.AddressFormatException;
import com.mxw.exceptions.JsonRpcClientException;
import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;
import com.mxw.protocol.Service;
import com.mxw.protocol.common.Request;
import com.mxw.protocol.common.Response;
import com.mxw.protocol.http.HttpService;
import com.mxw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class JsonRpcProvider extends BaseProvider {

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);


    private Service service;

    public JsonRpcProvider(String url) {
        this(new HttpService(url));
    }

    public JsonRpcProvider(String url, String chainId) {
        this(new HttpService(url), Network.getNetwork(chainId));
    }

    public JsonRpcProvider(String url, Network network) {
        this(new HttpService(url), network);
    }

    public JsonRpcProvider(Service service) {
       this(service, null);
    }

    public JsonRpcProvider(Service service, Network network) {
        super(network);
        this.service = service;
    }

    @Override
    protected <T> T perform(String method, Class<?> responseType, Object ... params) {
        return this.perform(method, (Type) responseType, params);
    }

    @Override
    protected <T> T perform(String method, Type responseType, Object ... params)  {

        String m = refactorMethodName(method);
        Request<?, ?> request = new Request(m,Arrays.asList(params), service, responseType.getClass());

        try {
            Response response = this.service.send(request, responseType);
            if(log.isDebugEnabled() && response.isHasResult()){
                log.debug(response.getRawResponse());
            }
            if(response.hasError()){
                String message = !Strings.isEmpty(response.getError().getData()) ? response.getError().getData() : response.getError().getMessage();
                handleErrorResponse(response.getError().getCode(),message);
            }
            return (T) response.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void handleErrorResponse(int code, String message) {
        if(message.contains("decoding bech32 failed")) {
            throw  new AddressFormatException("invalid address");
        }else if(message.startsWith("Tx (") && message.contains(") not found")) {
            throw new TransactionException("transaction not found");
        }else if(message.contains("\"codespace\":\"mxw\",\"code\":1000,") || code == 1000){
            throw new TransactionException("kyc registration is required");
        }else if(message.contains("\"codespace\":\"mxw\",\"code\":1001,") || code == 1001){
            throw new TransactionException("duplicated kyc");
        }else if(message.contains("\"codespace\":\"sdk\",\"code\":5,") || message.contains("\"codespace\":\"sdk\",\"code\":10,") || code == 5 || code == 10){
            throw new TransactionException("insufficient funds");
        }else if(message.contains("\"codespace\":\"sdk\",\"code\":14,") || code == 14){
            throw new TransactionException("insufficient fees");
        }else if(message.contains("\"codespace\":\"sdk\",\"code\":11,") || code == 11){
            throw new TransactionException("invalid amount");
        }else if(message.contains("signature verification failed") || code == 4) {
            throw new TransactionException("signature verification failed");
        }else if(message.contains("Height must be less than or equal to the current blockchain height")){
            throw new TransactionException("block not found");
        }

        super.handleErrorResponse(code, message);
    }

    protected String refactorMethodName(String origin) {
        switch (origin){
            case "getTransactionFeeSetting":
            case "getTokenList":
            case "isWhitelisted":
            case "getKycAddress":
            case "lookupAddress":
                return "abci_query";
            case "getAccountState":
                return "account";
            case "sendTransaction":
                return "encode_and_broadcast_tx_sync";
            case "sendTransactionAsync":
                return "encode_and_broadcast_tx_async";
            case "getBlock":
                return "block_results";
            case "getTransactionFee":
                return "query_fee";
            case "getTransactionReceipt":
                return "decoded_tx";
            default:
                return origin;
        }
    }

}
