package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class PayloadDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    private Class<?> resultClass;


    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec codec = jsonParser.getCodec();
        TreeNode node = codec.readTree(jsonParser);
        JsonParser parser = constructParser(node, jsonParser.getCodec());
        if(parser.getCurrentToken() != JsonToken.START_OBJECT || parser.getCurrentToken() != JsonToken.START_ARRAY){
            parser.nextToken();
        }
        return deserializationContext.readValue(parser, this.resultClass);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        this.resultClass = beanProperty.getType().getRawClass();
        return this;
    }

    private JsonParser constructParser(TreeNode node, ObjectCodec codec) throws IOException {
        if(node instanceof TextNode) {
            String json = ((TextNode) node).asText();
            return codec.getFactory().createParser(json);
        }else {
            return node.traverse();
        }
    }
}
