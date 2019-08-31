package com.mxw.providers;

import com.mxw.TestConfig;
import com.mxw.exceptions.AddressFormatException;
import com.mxw.exceptions.TransactionException;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.builder.BankSendBuilder;
import com.mxw.protocol.response.AccountState;
import com.mxw.protocol.response.TransactionFee;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.utils.Base64s;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;

public class JsonRpcProviderTest {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,true);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));

    @Test
    public void testValidAccount() {
        String address = "mxw1ff32lgz0pffjgxvlgg38jz9p7pgunhkvnhx54q";
        AccountState state = jsonRpcProvider.getAccountState(address);
        Assert.assertEquals(state.getType(),"cosmos-sdk/Account");
        Assert.assertEquals(state.getValue().getAddress(), address);
        AccountState validButNullState = jsonRpcProvider.getAccountState(TestConfig.VALID_ADDRESS_BUT_NULL);
        Assert.assertNull(validButNullState);
    }

    @Test
    public void testInvalidAccount() {
        try {
            jsonRpcProvider.getAccountState(TestConfig.INVALID_ADDRESS);
        }catch (AddressFormatException ex) {
            Assert.assertEquals(ex.getMessage(),"invalid address");
            return;
        }
        Assert.fail("no exception");
    }

    @Test
    public void testGetTransactionReceipt(){
        String hash = TestConfig.HTTP_TEST_DECODE_HASH;
        TransactionReceipt<TransactionRequest> receipt = jsonRpcProvider.getTransactionReceipt(hash, TransactionRequest.class);
        Assert.assertEquals(hash, receipt.getHash());
        Assert.assertNotNull(receipt.getPayload().getValue().getMsg().get(0).getValue());
    }

    @Test
    public void testGetTransactionReceiptNotFound(){
        String hash = TestConfig.HTTP_TEST_DECODE_HASH + "123";
        try {
          jsonRpcProvider.getTransactionReceipt(hash, TransactionRequest.class);
        }catch (TransactionException ex) {
            Assert.assertEquals(ex.getMessage(),"transaction not found");
        }
    }

    @Test
    public void testisWhiteListed() {
        Boolean whiteListed = jsonRpcProvider.isWhiteListed(TestConfig.PRIVATE_KEY_ADDRESS);
        Assert.assertNotNull(whiteListed);
    }

    @Test
    public void testNotWhiteListed() {
        Boolean whiteListed = jsonRpcProvider.isWhiteListed(TestConfig.VALID_ADDRESS_BUT_NULL);
        Assert.assertFalse(whiteListed);
    }

    @Ignore
    @Test
    public void testKycAddress() {
        String kycAddress = jsonRpcProvider.getKycAddress(TestConfig.PRIVATE_KEY_ADDRESS);
        Assert.assertEquals(kycAddress,TestConfig.PRIVATE_KEY_ADDRESS);
        String kycAddress2 = jsonRpcProvider.getKycAddress(TestConfig.VALID_ADDRESS_BUT_NULL);
        Assert.assertNull(kycAddress2);
    }

    @Test
    public void testGetFree() {
        String from = TestConfig.PRIVATE_KEY_ADDRESS;
        String to = TestConfig.TO_ADDRESS;
        BigInteger amount = BigInteger.valueOf(1 * 18);
        TransactionRequest request = jsonRpcProvider.getTransactionRequest("bank","bank-send", new BankSendBuilder(from,to, amount, null));
        TransactionFee fee = jsonRpcProvider.getTransactionFee(null, null, request);
        Assert.assertTrue(fee.getAmount().size() > 0 && fee.getAmount().get(0).getAmount()!=null);
    }

}
