package com.mxw.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.swing.tree.TreeNode;
import java.io.IOException;

public class KeepAsJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        TreeNode tree = jp.getCodec().readTree(jp);
        return tree.toString();
    }
}
