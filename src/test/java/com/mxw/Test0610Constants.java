package com.mxw;

import com.mxw.crypto.Keys;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.response.NodeInfo;
import com.mxw.protocol.response.Status;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import org.junit.Test;

public class Test0610Constants {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

    @Test
    public void version(){

        Status status = jsonRpcProvider.getStatus();

        NodeInfo nodeInfo = status.getNodeInfo();

        System.out.println(" Version = " + nodeInfo.getVersion());

        assert(nodeInfo.getVersion() != null);

    }

    // Constant variables should exists.
}