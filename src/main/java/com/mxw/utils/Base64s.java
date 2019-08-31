package com.mxw.utils;


import java.util.Base64;

public class Base64s {

    public static String decode(String encoded) {
        return decode(encoded.getBytes());
    }

    public static String decode(byte[] encoded) {
        byte[] data = Base64.getDecoder().decode(encoded);
        return new String(data);
    }

    public static String encode(String data) {
        return encode(data.getBytes());
    }

    public static String encode(byte[] data) {
       return Base64.getEncoder().encodeToString(data);
    }

    public static boolean isValidBase64(final String b64String) {
        //   ([0-9a-zA-Z+/]{4})*      # Groups of 4 valid characters decode
        //                            # to 24 bits of data for each group
        //   (                        # ending with:
        //   ([0-9a-zA-Z+/]{3}=)      # three valid characters followed by =
        return b64String.matches("(?:[A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=)");
    }

}
