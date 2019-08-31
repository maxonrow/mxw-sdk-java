package com.mxw.utils;

import com.mxw.Constants;
import com.mxw.crypto.Hash;

import java.math.BigInteger;

public class Address {

    public static String getAddress(String address) {
        if(Strings.isEmpty(address)){
            throw new IllegalArgumentException("Invalid Address");
        }

        String result = null;
        if(address.startsWith(Constants.AddressPrefix)){
            result = address;
        }
        else if(address.matches("^(0x)?[0-9a-fA-F]{40}$")) {
            address = Numeric.prependHexPrefix(address);

            result = getChecksum(address);

            if(address.matches("([A-F].*[a-f])|([a-f].*[A-F])") && !result.equals(address)){
                throw new IllegalArgumentException("bad address checksum");
            }
        }
        return result;
    }


    public static String getChecksum(String address) {
        if(Strings.isEmpty(address)){
            throw new IllegalArgumentException("Invalid Address");
        }

        String lowercaseAddress = Numeric.cleanHexPrefix(address).toLowerCase();
        String addressHash = Numeric.cleanHexPrefix(Hash.sha3String(lowercaseAddress));

        StringBuilder result = new StringBuilder(lowercaseAddress.length() + 2);

        result.append("0x");

        for (int i = 0; i < lowercaseAddress.length(); i++) {
            if (Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16) >= 8) {
                result.append(String.valueOf(lowercaseAddress.charAt(i)).toUpperCase());
            } else {
                result.append(lowercaseAddress.charAt(i));
            }
        }
        return result.toString();
    }

    public static String getHash(byte[] bytes) {
        return getChecksum(Numeric.toHexString(bytes));
    }

    public static String deriveAddress(String from, String nonce) {
        return deriveAddress(from, new BigInteger(nonce));
    }

    public static String deriveAddress(String from, Integer nonce) {
        return deriveAddress(from, BigInteger.valueOf(nonce));
    }


    public static String deriveAddress(String from, BigInteger nonce) {
        return deriveAddress(from, nonce.toByteArray());
    }


    public static String deriveAddress(String from, byte[] nonce) {
        if(Strings.isEmpty(from)){
            throw new IllegalArgumentException("from");
        }

        if(nonce==null) {
            throw new IllegalArgumentException("nonce");
        }
        return getHash(Hash.sha256(Bytes.concat(Numeric.hexStringToByteArray(getAddress(from)), Bytes.trimLeadingZeroes(nonce))));
    }
}
