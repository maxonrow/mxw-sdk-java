package com.mxw;

import com.mxw.crypto.Keys;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.response.AccountState;
import com.mxw.protocol.response.Coin;
import com.mxw.providers.JsonRpcProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class Test0600Utils {

    // set test bytes
    // test shallow copy propertiess
    // test camelize
    // test unit convert to blockchain format -- no need
    // test unit convert to blockchain format with 18 decimals -- no need
    // test unit convert to readable format
    // test unit convert to readable format with 18 decimals

    // normalizeBech32Address
    // normalizeHexAddress
    // generateRandomHashValue

    /**
     * Other tests
     */

    private HttpService httpService;
    private JsonRpcProvider jsonRpcProvider;

    private Wallet wallet;

    private String privateKey;
    private String toAddress;

    private BlockTag blockTag;
    private String addressOrName;
    private AccountState accountState;

    @Before
    public void before() {

        this.httpService = new HttpService(TestConfig.HTTP_SERVICE_URL, false);
        this.privateKey = TestConfig.PRIVATE_KEY_STRING;
        this.toAddress = TestConfig.TO_ADDRESS;
        this.jsonRpcProvider = new JsonRpcProvider(this.httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK, TestConfig.HTTP_SERVICE_NETWORK));

        this.accountState = jsonRpcProvider.getAccountState(TestConfig.PRIVATE_KEY_ADDRESS);
    }

    @Test
    public void resolveName(){
       // String address = baseProvider.resolveName(TestConfig.PRIVATE_KEY_ADDRESS);
        String address = jsonRpcProvider.resolveName(TestConfig.TO_ADDRESS);
        assert(address != null);
    }


    @Test
    public void verifyBalance() {

        BigInteger balance = BigInteger.ZERO;

        System.out.println(accountState.getValue() != null);
        System.out.println(accountState.getValue().getCoins() != null);

        if (accountState != null && accountState.getValue() != null && accountState.getValue().getCoins() != null) {

            List<Coin> coins = accountState.getValue().getCoins();

            if (coins.size() > 0) {

                if (coins.get(0).getAmount() != null)
                    balance = coins.get(0).getAmount();
            }
        }

        System.out.println("balance = " + balance);

        assert(balance.compareTo(BigInteger.ZERO) >= 0);
    }

    @Test
    public void getTransactionCount() {

        BigInteger sequence = BigInteger.ZERO;

        if (accountState != null && accountState.getValue() != null && accountState.getValue().getSequence() != null) {
            sequence = accountState.getValue().getSequence();
        }

        System.out.println("sequence = " + sequence);

        assert(sequence.compareTo(BigInteger.ZERO) >= 0);
    }
}
