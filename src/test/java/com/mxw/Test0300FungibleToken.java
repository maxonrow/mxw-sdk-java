package com.mxw;

import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;

public class Test0300FungibleToken {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));
}