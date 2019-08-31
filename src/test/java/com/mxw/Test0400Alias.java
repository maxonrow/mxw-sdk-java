package com.mxw;

import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.response.TransactionResponse;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.utils.Strings;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;

public class Test0400Alias {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

    /**
     * Suite Alias
     */

    /*
    @Test
    public void suiteAlias(){

    }

    @Test
    public void suiteAlias_Initialize(){

    }*/

    /**
     * Suite: Alias - Approve
     */

    /*
    @Test
    public void suiteAliasApprove(){

    }

    @Test
    public void suitealias_applicationShouldNotExists(){

    }

    @Test
    public void suiteAliasApprove_create(){

    }

    @Test
    public void suiteAliasApprove_createCheckDuplication(){

    }

    @Test
    public void suiteAliasApprove_resolveExpectedNoResult(){

    }

    @Test
    public void suiteAliasApprove_applicationShouldExists(){

    }

    @Test
    public void suiteAliasApprove_approve(){

    }

    @Test
    public void suiteAliasApprove_approveCheckDuplication(){

    }

    @Test
    public void suiteAliasApprove_resolveWithAlias(){

    }

    @Test
    public void suiteAliasApprove_lookupAddress(){

    }

    @Test
    public void suiteAliasApprove_applicationShouldNotExists(){

    }

    @Test
    public void suiteAliasApprove_checkProviderNonce(){

    }*/

    /**
     * Suite: Alias - Reject
     */

    /*
    @Test
    public void suiteAliasReject(){

    }

    @Test
    public void suiteAliasReject_applicationShouldNotExists(){

    }

    @Test
    public void suiteAliasReject_create(){

    }

    @Test
    public void suiteAliasReject_createCheckDuplication(){

    }

    @Test
    public void suiteAliasReject_resolveExpectedNoResult(){

    }

    @Test
    public void suiteAliasReject_applicationShouldExists(){

    }

    @Test
    public void suiteAliasReject_reject(){

    }

    @Test
    public void suiteAliasReject_rejectCheckDuplciation(){

    }

    @Test
    public void suiteAliasReject_resolveWithAlias(){

    }

    @Test
    public void suiteAliasReject_suiteAliasReject_lookupAddress(){

    }

    @Test
    public void cleanUp(){

    }*/
}