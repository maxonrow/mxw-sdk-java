package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.mxw.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerDeserializer extends JsonDeserializer<BigInteger> {
    @Override
    public BigInteger deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if(!node.get("_hex").isNull()) {
            String text = node.get("_hex").asText();
            return Numeric.decodeQuantity(text);
        }else if(!node.isObject()) {
            return new BigInteger(node.asText());
        }
        return BigInteger.ZERO;
    }
}
