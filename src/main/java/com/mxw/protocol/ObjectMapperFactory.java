package com.mxw.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.mxw.protocol.common.Response;
import com.mxw.protocol.deserializer.RawResponseDeserializer;
import com.mxw.protocol.deserializer.TransactionMessageDeserializer;
import com.mxw.protocol.deserializer.TransactionReceiptDeserializer;
import com.mxw.protocol.response.TransactionMessage;
import com.mxw.protocol.serializer.BigIntegerSerializer;
import com.mxw.protocol.response.TransactionReceipt;

import java.math.BigInteger;

/** Factory for managing our ObjectMapper instances.
 *
 *  adapted from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/protocol/ObjectMapperFactory.java">web3j</a>
 * */
public class ObjectMapperFactory {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    static {
        configureObjectMapper(DEFAULT_OBJECT_MAPPER, false);
    }

    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(false);
    }

    public static ObjectMapper getObjectMapper(boolean shouldIncludeRawResponses) {
        if (!shouldIncludeRawResponses) {
            return DEFAULT_OBJECT_MAPPER;
        }

        return configureObjectMapper(new ObjectMapper(), true);
    }

    public static ObjectReader getObjectReader() {
        return DEFAULT_OBJECT_MAPPER.reader();
    }

    private static ObjectMapper configureObjectMapper(
            ObjectMapper objectMapper, boolean shouldIncludeRawResponses) {
        SimpleModule module = new SimpleModule();

            module.setDeserializerModifier(
                    new BeanDeserializerModifier() {
                        @Override
                        public JsonDeserializer<?> modifyDeserializer(
                                DeserializationConfig config,
                                BeanDescription beanDesc,
                                JsonDeserializer<?> deserializer) {
                            if (shouldIncludeRawResponses) {
                                if (Response.class.isAssignableFrom(beanDesc.getBeanClass())) {
                                    return new RawResponseDeserializer(deserializer);
                                }
                            }

                            if(TransactionReceipt.class.isAssignableFrom(beanDesc.getBeanClass())){
                                return new TransactionReceiptDeserializer(deserializer);
                            }

                            if(TransactionMessage.class.isAssignableFrom(beanDesc.getBeanClass())){
                                return new TransactionMessageDeserializer(deserializer);
                            }

                            return deserializer;
                        }
                    });

            module.setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (BigInteger.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new BigIntegerSerializer();
                }
                return serializer;
            }
        });
        objectMapper.registerModule(module);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper;
    }
}
