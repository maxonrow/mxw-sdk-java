package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxw.protocol.ObjectMapperFactory;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.protocol.request.TransactionRequest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class TransactionReceiptDeserializerTest {

    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper(true);

    @Test
    public void testDeserialize() throws IOException {
        String json = "{\n" +
                "  \"hash\": \"0x21a888a485bb00ebf8d989a8d66feb8df236fe26ee761192867babeccde6c4e2\",\n" +
                "  \"status\": 1,\n" +
                "  \"index\": 0,\n" +
                "  \"blockNumber\": 118,\n" +
                "  \"result\": {\n" +
                "    \"events\": [\n" +
                "      {\n" +
                "        \"hash\": \"0x2cadcfb0c336769d503d557b26fcf1e91819e7e5\",\n" +
                "        \"params\": [\n" +
                "          \"mxw1edl2ef5c45acvn7062lgz53szyspqx7psxrm4w\",\n" +
                "          \"mxw1hl456zwwc2vl3rq6p4y2amhvpn8znhzt9axn35\",\n" +
                "          \"1000000000000\"\n" +
                "        ],\n" +
                "        \"address\": \"mxw1edl2ef5c45acvn7062lgz53szyspqx7psxrm4w\",\n" +
                "        \"transactionIndex\": 0,\n" +
                "        \"eventIndex\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"logs\": [{ \"success\": true, \"info\": {} }]\n" +
                "  },\n" +
                "  \"payload\": {\n" +
                "    \"type\": \"cosmos-sdk/StdTx\",\n" +
                "    \"value\": {\n" +
                "      \"fee\": {\n" +
                "        \"amount\": [{ \"amount\": \"10000000000000000\", \"denom\": \"cin\" }],\n" +
                "        \"gas\": \"0\"\n" +
                "      },\n" +
                "      \"memo\": \"testing\",\n" +
                "      \"msg\": [\n" +
                "        {\n" +
                "          \"type\": \"mxw/msgSend\",\n" +
                "          \"value\": {\n" +
                "            \"amount\": [{ \"amount\": \"1000000000000\", \"denom\": \"cin\" }],\n" +
                "            \"fromAddress\": \"mxw1edl2ef5c45acvn7062lgz53szyspqx7psxrm4w\",\n" +
                "            \"toAddress\": \"mxw1hl456zwwc2vl3rq6p4y2amhvpn8znhzt9axn35\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"signatures\": [\n" +
                "        {\n" +
                "          \"signature\": \"eUh2DUscq8azhfRk2buxGEt2HCaw9XJ38EP39xKTywlzvBwxEAiw0Mcs5i+e4+lvzIHkB1LfxxlweukIN2EsCA==\",\n" +
                "          \"pubKey\": {\n" +
                "            \"type\": \"tendermint/PubKeySecp256k1\",\n" +
                "            \"value\": \"A1uXX0v94nB2fjzp0A0TKvFLR68rOLdhzmrJWoWq7IC6\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"nonce\": { \"_hex\": \"0x00\" },\n" +
                "  \"confirmations\": 1\n" +
                "}\n";

        JavaType type = objectMapper.getTypeFactory().constructParametricType(TransactionReceipt.class, TransactionRequest.class);
        TransactionReceipt<TransactionRequest> receipt = objectMapper.readValue(json, type);
        Assert.assertEquals(receipt.getHash(),"0x21a888a485bb00ebf8d989a8d66feb8df236fe26ee761192867babeccde6c4e2");
        Assert.assertEquals(receipt.getPayload().getValue().getMemo(),"testing");
        Assert.assertEquals(receipt.getBlockNumber(), new BigInteger("118"));
    }

}
