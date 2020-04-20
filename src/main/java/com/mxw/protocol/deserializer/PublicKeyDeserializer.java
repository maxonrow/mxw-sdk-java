package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.mxw.utils.Numeric;

import java.io.IOException;
import java.util.Base64;

public class PublicKeyDeserializer extends JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if(node.isTextual()) {
            return node.toString();
        }else if(node.isObject()){
            if(node.get("value")!=null) {
                String value = node.get("value").textValue();
                byte[] data = Base64.getDecoder().decode(value);
                return Numeric.toHexString(data);
            }
            return node.toString();
        }
        return "";
    }


}
