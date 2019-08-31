package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mxw.utils.Base64s;
import com.mxw.utils.Strings;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Base64Deserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    private Class<?> resultClass;

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();

        try {
            String decoded = Base64s.decode(value);

            if(Strings.jsJsonString(decoded)){
                JsonParser objectParser = jsonParser.getCodec().getFactory().createParser(decoded);
                return deserializationContext.readValue(objectParser, this.resultClass);
            }
          return toKnowClass(decoded);
        }catch (IllegalArgumentException | JsonParseException e) {
            String fieldName = jsonParser.getParsingContext().getCurrentName();
            Class<?> wrapperClass = jsonParser.getParsingContext().getCurrentValue().getClass();

            throw new InvalidFormatException(
                    jsonParser,
                    String.format("Value for '%s' is not a base64 encoded JSON", fieldName),
                    value,
                    wrapperClass
            );
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        this.resultClass = beanProperty.getType().getRawClass();
        return this;
    }

    private Object toKnowClass(String value) {
        if(resultClass.isAssignableFrom(Boolean.class))
            return  new Boolean(value);
        else if(resultClass.isAssignableFrom(Integer.class))
            return new Integer(value);
        else if(resultClass.isAssignableFrom(BigInteger.class))
            return new BigInteger(value);
        else if(resultClass.isAssignableFrom(BigDecimal.class))
            return new BigDecimal(value);
        else if(resultClass.isAssignableFrom(Long.class))
            return new Long(value);
        else
            return value;

    }
}
