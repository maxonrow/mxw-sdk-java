package com.mxw.networks;

import org.junit.Assert;
import org.junit.Test;

public class NetworkTest {

    @Test
    public void testInstantiateNetwork() {
        Network testnet = Networks.ALLOYS.getNetwork();
        Assert.assertEquals(testnet.getChainId(),"alloys");
        Network mainnet = Networks.MAINNET.getNetwork();
        Assert.assertEquals(mainnet.getChainId(),"maxonrow");
        Network homestead = Networks.HOMESTEAD.getNetwork();
        Assert.assertEquals(homestead.getChainId(), "maxonrow");
      //  Assert.assertNotNull(homestead.getDefaultProvider());

        Network n = Network.getNetwork("testnet");
       Assert.assertEquals(n.getChainId(),"testnet");
    }


}
