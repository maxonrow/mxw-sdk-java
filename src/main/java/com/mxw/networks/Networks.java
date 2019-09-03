package com.mxw.networks;

import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;

public enum Networks {

    HOMESTEAD("mxw", "mxw"),
    MAINNET("mxw", "mxw"),
    ALLOYS("alloys", "alloys"),
    UNSPECIFED("0", "unspecified");

    private Network network;

    Networks(String chainId, String name) {
        this.network = new Network(chainId, name);
    }

    public Network getNetwork() {
        return this.network;
    }

    public Provider getDefaultProvider() {
        String chainId = this.network.getChainId();
        if(chainId.equalsIgnoreCase("mxw"))
            return mxwDefaultProvider("mxw");
        else if (chainId.equalsIgnoreCase("alloys"))
            return etcDefaultProvider("https://alloys.maxonrow.com", chainId);

        return null;
    }

    private static Provider mxwDefaultProvider(String chainId) {
        return mxwDefaultProvider(new Network(chainId, chainId));
    }

    private static Provider mxwDefaultProvider(Network network) {
        return new JsonRpcProvider("https://pub-rpc.maxonrow.com", network);
    }

    private static Provider etcDefaultProvider(String url, String chainId) {
        return  new JsonRpcProvider(url, chainId);
    }
}
