package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mxw.protocol.common.Response;
import com.mxw.protocol.request.messages.BankSend;
import com.mxw.protocol.response.TransactionMessage;

import java.io.IOException;

public class TransactionMessageDeserializer extends StdDeserializer<TransactionMessage> implements ResolvableDeserializer {

    private final JsonDeserializer<?> defaultDeserializer;

    public TransactionMessageDeserializer(JsonDeserializer<?> defaultDeserializer) {
        super(Response.class);
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public TransactionMessage deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec codec = jsonParser.getCodec();
        if (codec==null)
            return null;
        JsonNode node = codec.readTree(jsonParser);
        JsonParser messageParser = node.traverse();
        if(messageParser.getCurrentToken() != JsonToken.START_OBJECT || messageParser.getCurrentToken() != JsonToken.START_ARRAY){
            messageParser.nextToken();
        }

        TransactionMessage message = (TransactionMessage) defaultDeserializer.deserialize(messageParser, ctx);

        JsonNode valueNode = node.get("value");
        if(!valueNode.isNull()) {
            JsonParser valueParser = valueNode.traverse();
            if(valueParser.getCurrentToken() != JsonToken.START_OBJECT || valueParser.getCurrentToken() != JsonToken.START_ARRAY){
                valueParser.nextToken();
            }
            Object value = ctx.readValue(valueParser, getGenericClassByType(message.getType()));
            //noinspection unchecked
            message.setValue(value);
        }

        return message;
    }

    @Override
    public void resolve(DeserializationContext deserializationContext) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(deserializationContext);
    }

    private Class<?> getGenericClassByType(String type) {
        //TODO: add kyc/whitelist etc
        switch (type){
            case "mxw/msgSend":
                return BankSend.class;
            default:
                return Object.class;
        }
    }
}
