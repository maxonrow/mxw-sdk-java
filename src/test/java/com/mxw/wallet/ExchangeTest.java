package com.mxw.wallet;

import com.mxw.TestConfig;
import com.mxw.networks.Network;
import com.mxw.protocol.http.HttpService;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.request.messages.BankSend;
import com.mxw.protocol.response.*;
import com.mxw.providers.JsonRpcProvider;
import com.mxw.providers.Provider;
import com.mxw.utils.Convert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ExchangeTest {

    private HttpService httpService = new HttpService(TestConfig.HTTP_SERVICE_URL,false);
    private Provider jsonRpcProvider = new JsonRpcProvider(httpService, new Network(TestConfig.HTTP_SERVICE_NETWORK,TestConfig.HTTP_SERVICE_NETWORK));

  @Ignore
    @Test
    public void testGetBlocks() {
        String depositAddress = TestConfig.TO_ADDRESS;

        BigInteger startBlock = new BigInteger("1");
        BigInteger endBlock = new BigInteger("842");

        while (startBlock.compareTo(endBlock) < 0){
            Block block = jsonRpcProvider.getBlock(startBlock);
            startBlock = startBlock.add(BigInteger.ONE);
            List<BlockTransaction> transactions = block.getResults().getTransactions();
            if(transactions.size() <= 0)
                continue;

            for (BlockTransaction transaction : transactions) {
                if(transaction.getLogs()==null || transaction.getLogs().size() == 0){
                    continue;
                }
                TransactionLog log = transaction.getLogs().get(0);
                if(log==null || !log.getSuccess())
                    continue;

                List<TransactionEvent> events = transaction.getEvents();
                if(events==null || events.size() == 0)
                    continue;

                TransactionEvent event = events.get(0);
                if(event==null)
                    continue;

                // 0x2cadcfb0c336769d503d557b26fcf1e91819e7e5 represent transfer
                if(!event.getHash().equalsIgnoreCase("0x2cadcfb0c336769d503d557b26fcf1e91819e7e5"))
                    continue;
                List<String> params = event.getParams();
                String from = params.get(0);
                String to = params.get(1);
                String amount = params.get(2);

                if(!to.equalsIgnoreCase(depositAddress))
                    continue;

                TransactionReceipt<TransactionRequest> receipt = this.jsonRpcProvider.getTransactionReceipt(transaction.getHash(), TransactionRequest.class);

                // fail to deserialize TransactionRequest payload not matched
                if(receipt.getPayload()==null || receipt.getPayload().getValue()==null || receipt.getStatus()==null || receipt.getStatus()!=1) {
                    continue;
                }
                String  memo = receipt.getPayload().getValue().getMemo();

                List<TransactionMessage> messages = receipt.getPayload().getValue().getMsg();

                    TransactionMessage message = messages.get(0);
                    if(message.getValue()==null || !message.getValue().getClass().isAssignableFrom(BankSend.class))
                        continue;

                    BankSend bankSend = (BankSend) message.getValue();
                    if(!bankSend.getToAddress().equalsIgnoreCase(depositAddress)){
                        continue;
                    }
                    List<Coin> coins = bankSend.getAmount();
                    Coin coin = coins.get(0);

                    // amount not match
                    if(!coin.getAmount().equals(new BigInteger(amount)) && !coin.getDenom().equalsIgnoreCase(Convert.Unit.CIN.toString()))
                        continue;

                    handleDeposit(bankSend, memo, coin, transaction.getHash());
            }

        }


    }

    private String getUserIdFromMemo(String memo) {
        return "userId" + memo;
    }

    private void handleDeposit(BankSend send, String memo, Coin coin, String hash) {
        String fakeUserId = getUserIdFromMemo(memo);
        BigDecimal value = Convert.fromCIN(new BigDecimal(coin.getAmount().toString()), Convert.Unit.MXW);
        if(fakeUserId!=null){
            addSuccessDeposit(fakeUserId, send.getFromAddress(), value, hash);
        }else {
            handleWrongMemo(memo, send.getFromAddress(), value, hash);
        }
    }

    private void addSuccessDeposit(String userId, String fromAddress, BigDecimal amount, String hash) {
        // simple add deposit
        System.out.println("received " + amount.toString() + " from " + fromAddress + " with user " + userId + " hash:" + hash);
    }

    private void handleWrongMemo(String memo, String fromAddress, BigDecimal amount, String hash) {
        System.out.println("received " + amount.toString() + " from " + fromAddress + " with unknown user hash:" + hash);
    }

}
