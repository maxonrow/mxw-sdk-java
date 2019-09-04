package com.mxw.tx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.TestConfig;
import com.mxw.crypto.SigningKey;
import com.mxw.networks.Network;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class TransactionManagerTest {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

    @Test
    public void testMultipleSignatures() throws JsonProcessingException {

        TransactionManager transactionManager1 = new DefaultTransactionManager(jsonRpcProvider, new SigningKey(TestConfig.PRIVATE_KEY_STRING));

        TransactionRequest request = transactionManager1.createTransaction(new BankSendBuilder(TestConfig.PRIVATE_KEY_ADDRESS, TestConfig.TO_ADDRESS, BigInteger.TEN, "1"));
        Assert.assertEquals(request.getValue().getSignatures().size(), 0);
        // sign first time
        transactionManager1.sign(request);
        Assert.assertEquals(request.getValue().getSignatures().size(), 1);
        // resign to check duplicate signatures
        transactionManager1.sign(request);
        // should remain one
        Assert.assertEquals(request.getValue().getSignatures().size(), 1);
        TransactionManager transactionManager2 = new DefaultTransactionManager(jsonRpcProvider, new SigningKey(TestConfig.TO_ADDRESS_PRIVATE_KEY));
        transactionManager2.sign(request);
        Assert.assertEquals(request.getValue().getSignatures().size(), 2);
    }

}
