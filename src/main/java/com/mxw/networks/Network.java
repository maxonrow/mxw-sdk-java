package com.mxw.networks;

import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.utils.Strings;

import java.math.BigInteger;

public class Network {

    public Network(String chainId) {
        this(chainId, chainId);
    }

    public Network(String chainId, String name) {
        this.chainId = chainId;
        this.name = name;
    }

    private String name;

    private String chainId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }


    public static Network getNetwork(String name) {
        return getNetwork((Object)name);
    }

    public static Network getNetwork(BigInteger chain) {
        return getNetwork((Object)chain);
    }

    public static Network getNetwork(Network network) {
        return getNetwork((Object)network);
    }

    private static Network getNetwork(Object network) {
        if(network==null)
            return null;

        if(network instanceof BigInteger || network instanceof String) {
            for (Networks net : Networks.values()) {
                Network n = net.getNetwork();
                if(n.chainId.equalsIgnoreCase(network.toString())){
                    return new Network(n.chainId, n.name);
                }
            }
            return new Network(network.toString(), "unknown");
        }

        Network thisNetwork = (Network) network;
        Network n = null;
        for (Networks net : Networks.values()) {
            Network i = net.getNetwork();
            if(i.chainId.equalsIgnoreCase(thisNetwork.chainId)){
                n = i;
                break;
            }
        }

        if(n==null) {
           return thisNetwork;
        }

        if(Strings.isEmpty(thisNetwork.chainId) && !thisNetwork.chainId.equalsIgnoreCase(n.chainId)){
            throw new IllegalArgumentException("network chainId mismatch");
        }

        return new Network(thisNetwork.name, n.chainId);
    }

    private static Provider mxwDefaultProvider(String chainId) {
        return new JsonRpcProvider("https://mainnet.mxw.one", chainId);
    }

    private static Provider etcDefaultProvider(String url, String chainId) {
        return  new JsonRpcProvider(url, chainId);
    }
}
