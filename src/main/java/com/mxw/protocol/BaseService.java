package com.mxw.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mxw.exceptions.InvalidResponseException;
import com.mxw.protocol.common.ReadContext;
import com.mxw.protocol.common.Request;
import com.mxw.protocol.common.Response;
import com.mxw.protocol.events.Notification;
import com.mxw.protocol.response.InnerResponse;
import com.mxw.utils.Strings;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/** Services API.
 *  adapted from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/protocol/Service.java">web3j</a>
 * */
public abstract class BaseService implements Service {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final ObjectMapper objectMapper;
    private static final String RESULT = "result";
    private static final String ERROR = "error";
    private static final String INNER_RESPONSE = "response";
    private static final String LOG = "log";
    private static final String VALUE = "value";
    private final boolean includeRawResponses;

    public BaseService(boolean includeRawResponses) {
        this.includeRawResponses = includeRawResponses;
        objectMapper = ObjectMapperFactory.getObjectMapper(includeRawResponses);
    }

    protected abstract InputStream performIO(String payload) throws IOException;

    @Override
    public <T> Response<T> send(Request request, Class<T> responseType) throws IOException {
        return send(request, objectMapper.constructType(responseType));
    }

    @Override
    public <T> Response<T> send(Request request, Type responseType) throws IOException {
        String payload = objectMapper.writeValueAsString(request);

        try (InputStream input = performIO(payload)) {
            ReadContext context = ReadContext.getReadContext(input, objectMapper);
            ObjectNode jsonObject = getValidResponse(context);
            Response<T> response;

            boolean hasResult = hasResult(jsonObject);
            JavaType resultType = objectMapper.constructType(Response.class);
            response = objectMapper.readValue(jsonObject.traverse(), resultType);
            response.setHasResult(hasResult);
            // temporary solution to add raw Response
            if(includeRawResponses && Strings.isEmpty(response.getRawResponse())) {
                response.setRawResponse(jsonObject.toString());
            }

            if (hasResult) {
                if (responseType == null || responseType == Void.class) {
                    return response;
                }
                JsonNode resultNode = jsonObject.get(RESULT);
                readResponse(response, responseType, resultNode);
                return response;
            }
            else {
                response.setResult(null);
                return response;
            }
        }
    }

    @Override
    public <T> CompletableFuture<Response<T>> sendAsync(
            Request jsonRpc20Request, Class<T> responseType) {
        return Async.run(() -> send(jsonRpc20Request, responseType));
    }

    @Override
    public <T extends Notification<?>> Flowable<Response<T>> subscribe(
            Request request, String unsubscribeMethod, Class<T> responseType) {
        throw new UnsupportedOperationException(
                String.format(
                        "Service %s does not support subscriptions",
                        this.getClass().getSimpleName()));
    }

    private boolean hasResult(ObjectNode jsonObject) {
        return jsonObject.has(RESULT) && !(jsonObject.get(RESULT).isNull() || jsonObject.get(RESULT).asText().equalsIgnoreCase("null"));
    }

    private ObjectNode getValidResponse(ReadContext context) throws IOException {
        JsonNode response = readResponseNode(context);
        if(!response.isObject()){
            throw new InvalidResponseException("Invalid JSON-RPC response");
        }

        return ObjectNode.class.cast(response);
    }

    private JsonNode readResponseNode(ReadContext context) throws IOException {
        context.assertReadable();
        JsonNode response = context.nextValue();
        logger.debug("JSON-PRC Response: {}", response);
        return response;
    }

    @SuppressWarnings("unchecked")
    private void readResponse(Response response, Type responseType, JsonNode resultNode) throws IOException {
        JavaType returnJavaType = objectMapper.getTypeFactory().constructType(responseType);
        if(!hasInnerResponse(resultNode)) {
            if(resultNode instanceof TextNode) {
                String value = resultNode.asText();
                if(value.startsWith("{") && value.endsWith("}"))
                    resultNode = objectMapper.readTree(value);
            }
            JsonParser returnJsonParser = objectMapper.treeAsTokens(resultNode);
            response.setResult(objectMapper.readValue(returnJsonParser, returnJavaType));
        } else{
            response.setHasResult(false);
            InnerResponse innerResponse = readInnerResponse(resultNode);

            if(innerResponse==null) {
                response.setError(new Response.Error(-1, "Unknown Error"));
            }else {
                if(innerResponse.getCode()==0 && !innerResponse.getResponse().isNull()) {
                    response.setHasResult(true);
                    JsonParser innerResponseParser = objectMapper.treeAsTokens(innerResponse.getResponse());
                    response.setResult(objectMapper.readValue(innerResponseParser, returnJavaType));
                }else {
                    response.setResult(null);
                    response.setError(new Response.Error(innerResponse.getCode(), innerResponse.getLog()));
                }
            }
        }
    }

    private InnerResponse readInnerResponse(JsonNode resultJsonObject) throws IOException {
        if(hasInnerResponse(resultJsonObject)) {
            JsonNode response = resultJsonObject.get(INNER_RESPONSE);
            InnerResponse innerResponse =  new InnerResponse(-1);
            if(hasNode(response,LOG) && !Strings.isEmpty(response.get(LOG).asText())) {
                JsonParser parser = objectMapper.treeAsTokens(response);
                innerResponse = this.objectMapper.readValue(parser, InnerResponse.class);
            }else {
                innerResponse.setCode(0);
                innerResponse.setResponse(response);
            }
            return innerResponse;
        }
        return null;
    }

    private boolean hasInnerResponse(JsonNode resultJsonObject) {
        return hasNode(resultJsonObject, INNER_RESPONSE);
    }

    private boolean hasNode(JsonNode value, String nodeKey) {
        return value.has(nodeKey) && !value.get(nodeKey).isNull();
    }


}
