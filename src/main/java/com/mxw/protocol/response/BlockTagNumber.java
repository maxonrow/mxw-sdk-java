/*
 * Copyright 2019 Web3 Labs LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.mxw.protocol.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mxw.protocol.request.BlockTag;
import com.mxw.utils.Numeric;

import java.math.BigInteger;

public class BlockTagNumber implements BlockTag {

    private BigInteger blockNumber;

    public BlockTagNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public BlockTagNumber(long blockNumber) {
        this(BigInteger.valueOf(blockNumber));
    }

    @Override
    @JsonValue
    public String getValue() {
        return Numeric.encodeQuantity(blockNumber);
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }
}
