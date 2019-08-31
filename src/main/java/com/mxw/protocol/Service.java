package com.mxw.protocol;

import com.mxw.protocol.common.Request;
import com.mxw.protocol.common.Response;
import com.mxw.protocol.events.Notification;
import io.reactivex.Flowable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/** Services API.
 *  adapted from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/protocol/Web3jService.java">web3j</a>
 * */
public interface Service {

    /**
     * Perform a synchronous JSON-RPC request.
     *
     * @param request request to perform
     * @param responseType class of a data item returned by the request
     * @param <T> type of a data item returned by the request
     * @return deserialized JSON-RPC response
     * @throws IOException thrown if failed to perform a request
     */
    <T> Response<T> send(Request request, Class<T> responseType) throws IOException;

    <T> Response<T> send(Request request, Type responseType) throws IOException;

    /**
     * Performs an asynchronous JSON-RPC request.
     *
     * @param request request to perform
     * @param responseType class of a data item returned by the request
     * @param <T> type of a data item returned by the request
     * @return CompletableFuture that will be completed when a result is returned or if a request
     *     has failed
     */
    <T> CompletableFuture<Response<T>> sendAsync(Request request, Class<T> responseType);

    /**
     * Subscribe to a stream of notifications. A stream of notifications is opened by by performing
     * a specified JSON-RPC request and is closed by calling the unsubscribe method. Different
     * WebSocket implementations use different pair of subscribe/unsubscribe methods.
     *
     * <p>This method creates an Flowable that can be used to subscribe to new notifications. When a
     * client unsubscribes from this Flowable the service unsubscribes from the underlying stream of
     * events.
     *
     * @param request JSON-RPC request that will be send to subscribe to a stream of events
     * @param unsubscribeMethod method that will be called to unsubscribe from a stream of
     *     notifications
     * @param responseType class of incoming events objects in a stream
     * @param <T> type of incoming event objects
     * @return a {@link Flowable} instance that emits incoming events
     */
    <T extends Notification<?>> Flowable<Response<T>> subscribe(
            Request request, String unsubscribeMethod, Class<T> responseType);

    /**
     * Closes resources used by the service.
     *
     * @throws IOException thrown if a service failed to close all resources
     */
    void close() throws IOException;

}
