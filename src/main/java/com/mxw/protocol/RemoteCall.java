package com.mxw.protocol;

import io.reactivex.Flowable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * A common type for wrapping remote requests.
 * adapted from <a href="https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/protocol/core/RemoteCall.java">web3j</a>
 * @param <T> Our return type.
 */
public class RemoteCall<T> {

    private Callable<T> callable;

    public RemoteCall(Callable<T> callable) {
        this.callable = callable;
    }

    /**
     * Perform request synchronously.
     *
     * @return result of enclosed function
     * @throws Exception if the function throws an exception
     */
    public T send() throws Exception {
        return callable.call();
    }

    /**
     * Perform request asynchronously with a future.
     *
     * @return a future containing our function
     */
    public CompletableFuture<T> sendAsync() {
        return Async.run(this::send);
    }

    /**
     * Provide an flowable to emit result from our function.
     *
     * @return an flowable
     */
    public Flowable<T> flowable() {
        return Flowable.fromCallable(this::send);
    }
}