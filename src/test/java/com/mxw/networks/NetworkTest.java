package com.mxw.networks;

import org.junit.Assert;
import org.junit.Test;

public class NetworkTest {

    @Test
    public void testInstantiateNetwork() {
        Network testnet = Networks.TESTNET.getNetwork();
        Assert.assertEquals(testnet.getChainId(),"testnet");
        Network mainnet = Networks.MAINNET.getNetwork();
        Assert.assertEquals(mainnet.getChainId(),"mxw");
        Network homestead = Networks.HOMESTEAD.getNetwork();
        Assert.assertEquals(homestead.getChainId(), "mxw");
      //  Assert.assertNotNull(homestead.getDefaultProvider());

        Network n = Network.getNetwork("testnet");
       Assert.assertEquals(n.getChainId(),"testnet");
    }


}
