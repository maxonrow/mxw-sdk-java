package com.mxw.providers;

import com.mxw.networks.Network;
import com.mxw.protocol.request.BlockTag;
import com.mxw.protocol.response.*;
import com.mxw.protocol.request.messages.builder.TransactionValueBuilder;
import com.mxw.protocol.request.TransactionRequest;
import com.mxw.protocol.response.fungibleToken.FungibleTokenState;
import com.mxw.protocol.response.nonFungibleToken.NFTokenState;

import java.math.BigInteger;

public interface Provider {

	Network getNetwork();

	BigInteger getBlockNumber();

	/**
	 *
	 * @param route
	 * @param transactionType
	 * @param builder         - to inject and resolve polyform value
	 * @return
	 */
	TransactionRequest getTransactionRequest(String route, String transactionType, TransactionValueBuilder builder);

	TransactionFee getTransactionFee(String route, String transactionType, TransactionRequest request);

	TransactionFeeSetting getTransactionFeeSetting(String transactionType);

	Status getStatus();

	TransactionFee getTokenTransactionFee(String symbol, String transactionType);

	TokenState getTokenState(String symbol, BlockTag blockTag);

	TokenList getTokenList(BlockTag blockTag);

	TokenAccountState getTokenAccountState(String symbol, String address, BlockTag blockTag);

	AccountState getAccountState(String addressOrName);

	AccountState getAccountState(String addressOrName, BlockTag blockTag);

	BigInteger getAccountNumber(String addressOrName);

	BigInteger getAccountNumber(String addressOrName, BlockTag blockTag);

	BigInteger getBalance(String addressOrName);

	BigInteger getBalance(String addressOrName, BlockTag blockTag);

	BigInteger getTransactionCount(String addressOrName);

	BigInteger getTransactionCount(String addressOrName, BlockTag blockTag);

	TransactionResponse sendTransaction(String signedTransaction, boolean async);

	Block getBlock(long height);

	Block getBlock(BigInteger height);

	Block getBlock(BlockTag blockHashOrBlockTag);

	TransactionReceipt getTransaction(String transactionHash);

	<T> TransactionReceipt<T> getTransactionReceipt(String transactionHash, Class<T> receiptType);

	Boolean isWhiteListed(String addressOrName);

	Boolean isWhiteListed(String addressOrName, BlockTag blockTag);

	String getKycAddress(String addressOrName);

	String getKycAddress(String addressOrName, BlockTag blockTag);

	String resolveName(String name);

	String resolveName(String name, BlockTag blockTag);

	String lookupAddress(String address);

	String lookupAddress(String address, BlockTag blockTag);

	String getAliasState(String address);

	String getAliasState(String address, BlockTag blockTag);

	TransactionReceipt waitForTransaction(String transactionHash, BigInteger confirmation);

	NFTokenState getNFTokenState(String symbol, BlockTag blockTag);

	FungibleTokenState getFungibleTokenState(String symbol, BlockTag blockTag);

}
