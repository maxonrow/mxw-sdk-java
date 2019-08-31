package com.mxw.utils;

import java.util.Arrays;

import static com.mxw.crypto.Hash.sha256;

/** Byte array utility functions. */
public class Bytes {

    private Bytes() {
    }

    public static byte[] trimLeadingBytes(byte[] bytes, byte b) {
        int offset = 0;
        for (; offset < bytes.length - 1; offset++) {
            if (bytes[offset] != b) {
                break;
            }
        }
        return Arrays.copyOfRange(bytes, offset, bytes.length);
    }

    public static byte[] trimLeadingZeroes(byte[] bytes) {
        return trimLeadingBytes(bytes, (byte) 0);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static Integer getHashLength(String data) {
        if (!Numeric.isValidHex(data) || (data.length() % 2) != 0) {
            return null;
        }
        return (data.length() - 2) / 2;
    }

}