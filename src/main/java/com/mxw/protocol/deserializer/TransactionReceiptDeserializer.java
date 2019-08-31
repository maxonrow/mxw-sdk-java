package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mxw.protocol.response.JsonTokenName;
import com.mxw.protocol.response.TransactionEvent;
import com.mxw.protocol.response.DeliverTransaction;
import com.mxw.protocol.response.TransactionLog;
import com.mxw.protocol.response.TransactionReceipt;
import com.mxw.protocol.response.TypeAttributes;
import com.mxw.utils.Base64s;
import com.mxw.utils.Numeric;
import com.mxw.utils.Strings;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionReceiptDeserializer extends StdDeserializer<TransactionReceipt>
        implements ResolvableDeserializer {

    private final JsonDeserializer<?> defaultDeserializer;

    public TransactionReceiptDeserializer(JsonDeserializer<?> defaultDeserializer) {
        super(TransactionReceipt.class);
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public TransactionReceipt deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        JsonParser parser = codec.getFactory().createParser(node.toString());
        if(parser.getCurrentToken() != JsonToken.START_OBJECT){
            parser.nextToken();
        }

        TransactionReceipt receipt = (TransactionReceipt) defaultDeserializer.deserialize(parser, deserializationContext);
        if(receipt==null  || receipt.getHash() == null || receipt.getBlockNumber() == null){
            return deserializeFromGetTransaction(receipt, node, codec, deserializationContext);
        }else {
            List<TransactionLog> logs = receipt.getResult().getLogs();
            DeliverTransaction deliverTransaction = new DeliverTransaction();
            if(logs!=null && logs.size() > 0 && logs.get(0) !=null) {
                deliverTransaction.setLog(logs.get(0));
            }
            deliverTransaction.setHash(receipt.getHash());
            deliverTransaction.setNonce(receipt.getNonce());
            receipt.setDeliverTransaction(deliverTransaction);
        }

        return receipt;
    }

    @Override
    public void resolve(DeserializationContext deserializationContext) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(deserializationContext);

    }

    private TransactionReceipt deserializeFromGetTransaction(TransactionReceipt receipt, JsonNode node, ObjectCodec codec, DeserializationContext ctx) throws IOException, JsonProcessingException {
        Integer transactionIndex = node.get("index").isNull() ? null : node.get("index").asInt();
        receipt.setBlockNumber(new BigInteger(node.get(JsonTokenName.HEIGHT).asText()));
        JsonNode txResultNode = node.get(JsonTokenName.TX_RESULT);
        if(txResultNode.isNull() || !txResultNode.isObject())
            return receipt;

        TransactionReceipt.Result result = parseResult(transactionIndex, txResultNode, codec, ctx);
        List<TransactionLog> logs = result.getLogs();
        if(logs==null)
            return receipt;

        TransactionLog log = logs.size() > 0 ? logs.get(0) : null;
        if(log==null || log.getInfo() == null || Strings.isEmpty(log.getInfo().getHash())){
            return receipt;
        }
        DeliverTransaction transaction = getDeliveryTransaction(log, new BigInteger(txResultNode.get(JsonTokenName.GAS_USED).asText()));
        receipt.setDeliverTransaction(transaction);
        receipt.setResult(result);
        receipt.setStatus(log.getSuccess() ? 1 : 0);
        receipt.setRawPayload(node.get("tx").asText());
        receipt.setHash(transaction.getHash());
        receipt.setNonce(transaction.getNonce());

        return receipt;
    }

    private DeliverTransaction getDeliveryTransaction(TransactionLog log, BigInteger gasUsed) {
        DeliverTransaction transaction = new DeliverTransaction();
        transaction.setHash(log.getInfo().getHash());
        transaction.setLog(log);
        transaction.setNonce(log.getInfo().getNonce());
        transaction.setGasUsed(gasUsed);
        return transaction;
    }

    private TransactionReceipt.Result parseResult(Integer transactionIndex, JsonNode jsonNode, ObjectCodec codec, DeserializationContext ctx) throws IOException {
        TransactionReceipt.Result result = new TransactionReceipt.Result();
        JsonNode logs = jsonNode.get(JsonTokenName.LOG);
        result.setRawLog(logs.asText());
        result.setLogs(parseLogs(logs, codec));
        result.setEvents(parseEvents(transactionIndex, jsonNode.get(JsonTokenName.EVENTS), codec, ctx));
        return result;
    }


    private List<TransactionLog> parseLogs(JsonNode jsonNode, ObjectCodec codec) throws IOException {
        List<TransactionLog> transactionLogs = new ArrayList<>();
        if(!jsonNode.isArray()){
            JsonParser jsonParser = codec.getFactory().createParser(jsonNode.asText());
            jsonNode = codec.readTree(jsonParser);
        }

        Iterator<JsonNode> nodesIterator = jsonNode.iterator();
        while(nodesIterator.hasNext()){
            JsonNode logNode = nodesIterator.next();
            TransactionLog log = new TransactionLog();
            log.setLog(logNode.get(JsonTokenName.LOG).asText());
            log.setMsgIndex(logNode.get(JsonTokenName.MSG_INDEX).asInt());
            log.setSuccess(logNode.get(JsonTokenName.SUCCESS).asBoolean());
            log.setInfo(parseInfo(logNode.get(JsonTokenName.LOG), codec));
            transactionLogs.add(log);
        }

        return transactionLogs;
    }

    private List<TransactionEvent> parseEvents(Integer transactionIndex, JsonNode jsonNode, ObjectCodec codec, DeserializationContext ctx) throws IOException {
        List<TransactionEvent> events = new ArrayList<>();
        if(!jsonNode.isArray()){
            return events;
        }

        JsonParser parser = jsonNode.traverse();
        if(parser.getCurrentToken() != JsonToken.START_ARRAY){
            parser.nextToken();
        }
        CollectionType listType = ctx.getTypeFactory().constructCollectionType(List.class, TypeAttributes.class);
        List<TypeAttributes> attributes =  ctx.readValue(parser, listType);
        for (TypeAttributes typeAttributes :
                attributes) {
            if (!typeAttributes.getType().equals("system"))
                continue;

            int eventIndex = 0;
            for (TypeAttributes.Attribute attribute : typeAttributes.getAttributes()) {
                TransactionEvent event;
                if(attribute.getValue() instanceof String) {
                    String base64 = attribute.getValue().toString();
                    String json = Base64s.decode(base64);
                    JsonParser eventParser = codec.getFactory().createParser(json);
                    if(eventParser.getCurrentToken()!=JsonToken.START_OBJECT){
                        eventParser.nextToken();
                    }
                    event = ctx.readValue(eventParser,TransactionEvent.class);
                    event.setAddress(Base64s.decode(attribute.getKey()));
                    event.setHash(Numeric.prependHexPrefix(event.getHash()));
                    event.setTransactionIndex(transactionIndex);
                    event.setEventIndex(eventIndex++);
                    events.add(event);
                }
            }
        }
        return events;
    }

    private TransactionLog.Info parseInfo(JsonNode jsonNode, ObjectCodec codec) throws IOException {
        if(jsonNode.isNull())
            return null;

        if(!jsonNode.isObject()) {
            String json = jsonNode.asText();
            if(Strings.isEmpty(json)){
                return null;
            }
            JsonParser jsonParser = codec.getFactory().createParser(json);
            jsonNode = codec.readTree(jsonParser);
        }

        TransactionLog.Info info = new TransactionLog.Info();
        if(!jsonNode.get(JsonTokenName.HASH).isNull()) {
            info.setHash(jsonNode.get(JsonTokenName.HASH).asText().toLowerCase());
            info.setNonce(BigInteger.valueOf(jsonNode.get(JsonTokenName.NONCE).asInt()));
        }

        return info;
    }
}

