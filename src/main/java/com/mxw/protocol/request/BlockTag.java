package com.mxw.protocol.request;

import com.mxw.protocol.response.BlockTagNumber;

import java.math.BigInteger;

public interface BlockTag {

    static BlockTag valueOf(BigInteger blockNumber) {
        if (BigInteger.ZERO.compareTo(blockNumber) >= 0) {
            blockNumber = BigInteger.ZERO;
        }
        return new BlockTagNumber(blockNumber);
    }

    static BlockTag valueOf(String blockName) {
        return BlockTagName.fromString(blockName);
    }

    String getValue();

}
