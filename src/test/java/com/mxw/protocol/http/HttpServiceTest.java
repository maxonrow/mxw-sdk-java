package com.mxw.protocol.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mxw.TestConfig;
import com.mxw.exceptions.ClientConnectionException;
import com.mxw.protocol.common.Request;
import com.mxw.protocol.response.AbciResponse;
import com.mxw.protocol.response.Status;
import com.mxw.protocol.response.Block;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.utils.Base64s;
import com.mxw.utils.Numeric;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HttpServiceTest {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,true);

    @Test
    public void testAddHeader() {
        String headerName = "customized_header0";
        String headerValue = "customized_value0";
        httpService.addHeader(headerName, headerValue);
        assertTrue(httpService.getHeaders().get(headerName).equals(headerValue));
    }

    @Test
    public void testAddHeaders() {
        String headerName1 = "customized_header1";
        String headerValue1 = "customized_value1";

        String headerName2 = "customized_header2";
        String headerValue2 = "customized_value2";

        HashMap<String, String> headersToAdd = new HashMap<>();
        headersToAdd.put(headerName1, headerValue1);
        headersToAdd.put(headerName2, headerValue2);

        httpService.addHeaders(headersToAdd);

        assertTrue(httpService.getHeaders().get(headerName1).equals(headerValue1));
        assertTrue(httpService.getHeaders().get(headerName2).equals(headerValue2));
    }

    @Test
    public void httpWebException() throws IOException {
        String content = "400 error";
        Response response =
                new Response.Builder()
                        .code(400)
                        .message("")
                        .body(ResponseBody.create(content, null))
                        .request(new okhttp3.Request.Builder().url(HttpService.DEFAULT_URL).build())
                        .protocol(Protocol.HTTP_1_1)
                        .build();

        OkHttpClient httpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(httpClient.newCall(Mockito.any()))
                .thenAnswer(
                        invocation -> {
                            Call call = Mockito.mock(Call.class);
                            Mockito.when(call.execute()).thenReturn(response);

                            return call;
                        });
        HttpService mockedHttpService = new HttpService(httpClient);

        Request<String, Status> request =
                new Request<>(
                        "status1",
                        Collections.emptyList(),
                        mockedHttpService,
                        Status.class);
        try {
           com.mxw.protocol.common.Response httpResponse =  mockedHttpService.send(request, Status.class);
        } catch (ClientConnectionException e) {
            Assert.assertEquals(
                    e.getMessage(),
                    "Invalid response received: " + response.code() + "; " + content);
            return;
        }

        Assert.fail("No exception");
    }

    @Test
    public void httpHasResult() {
        HttpService mockedHttpService = httpService;

        Request<String, Status> request =
                new Request<>(
                        "status",
                        Collections.emptyList(),
                        mockedHttpService,
                        Status.class);
        try {
            com.mxw.protocol.common.Response<Status> statusResponse = mockedHttpService.send(request, Status.class);
            Status status =  statusResponse.getResult();
            Assert.assertTrue(status.getSyncInfo().getLatestBlockHeight().compareTo(BigInteger.ZERO) > 0);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void httpTest_abciHasError() {
        HttpService mockedHttpService = httpService;

        String path = "/custom/fee/get_msg_fee_setting1/transfer";
        Request<String, AbciResponse> request =
                new Request<>(
                        "abci_query",
                        Arrays.asList(path, "", null, null),
                        mockedHttpService,
                        AbciResponse.class);
        try {
            com.mxw.protocol.common.Response<AbciResponse> response = mockedHttpService.send(request, AbciResponse.class);
            Assert.assertTrue(response.hasError());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void httpTestBlock() {
        HttpService mockedHttpService = httpService;
        BigInteger blockNumber = new BigInteger("173");
        Request<String, Block> request =
                new Request<>(
                        "block_results",
                        Arrays.asList(blockNumber.toString()),
                        mockedHttpService,
                        Block.class);
        try {
            com.mxw.protocol.common.Response<Block> response = mockedHttpService.send(request, Block.class);
            Assert.assertEquals(0, response.getResult().getBlockNumber().compareTo(blockNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void httpTestDecodeTx() {
        HttpService mockedHttpService = httpService;
        String hash = TestConfig.HTTP_TEST_DECODE_HASH;
        try {
        String base64 =   Base64s.encode(Numeric.hexStringToByteArray(hash.toLowerCase()));
        Request<String, TransactionReceipt> request =
                new Request<>(
                        "decoded_tx",
                        Arrays.asList(base64,null),
                        mockedHttpService,
                        TransactionReceipt.class);

            com.mxw.protocol.common.Response<TransactionReceipt> response = mockedHttpService.send(request, TransactionReceipt.class);
            assertEquals(hash, response.getResult().getHash());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
