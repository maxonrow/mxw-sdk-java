package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mxw.protocol.response.JsonTokenName;
import com.mxw.protocol.response.Block;
import com.mxw.protocol.response.BlockTransaction;
import com.mxw.protocol.response.TransactionEvent;
import com.mxw.protocol.response.TransactionLog;
import com.mxw.protocol.response.TypeAttributes;
import com.mxw.utils.Base64s;
import com.mxw.utils.Numeric;
import com.mxw.utils.Strings;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockResultDeserializer extends StdDeserializer<Block.Results> {

    private final static String LOG = "log";

    protected BlockResultDeserializer() {
        super(Block.Results.class);
    }

    @Override
    public Block.Results deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        Block.Results results = new Block.Results();
        JsonNode transactions = node.get(JsonTokenName.DELIVER_TRANSACTIONS);

        if(!transactions.isArray())
            return results;

        results.setTransactions(parseTransactions(transactions, codec, ctx));
        return results;
    }

   private List<BlockTransaction> parseTransactions(JsonNode jsonNode, ObjectCodec codec,DeserializationContext ctx) throws IOException {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int transactionIndex=0;
        List<BlockTransaction> blockTransactions = new ArrayList<>();
        while (nodeIterator.hasNext()){
            JsonNode transactionNode = nodeIterator.next();
            List<TransactionLog> logs = new ArrayList<>();
            JsonNode logsNode = transactionNode.get(JsonTokenName.LOG);
            if(!logsNode.isNull()){
                logs = parseLogs(logsNode,codec);
            }
            TransactionLog log = logs.size() > 0 ? logs.get(0) : null;
            if(log==null || log.getInfo() == null || Strings.isEmpty(log.getInfo().getHash())){
                continue;
            }

            BlockTransaction transaction = new BlockTransaction();
            transaction.setRawLog(logsNode.asText());
            transaction.setTransactionIndex(transactionIndex++);
            transaction.setGasUsed(BigInteger.valueOf(transactionNode.get(JsonTokenName.GAS_USED).asInt()));
            transaction.setLogs(logs);
            transaction.setHash(log.getInfo().getHash());
            transaction.setNonce(log.getInfo().getNonce());
            transaction.setEvents(parseEvents(transaction.getTransactionIndex(), transactionNode.get(JsonTokenName.EVENTS), codec, ctx));
            blockTransactions.add(transaction);
        }

        return blockTransactions;
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
            if(log.getSuccess()) {
                log.setInfo(parseInfo(logNode.get(JsonTokenName.LOG), codec));
                transactionLogs.add(log);
            }
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
