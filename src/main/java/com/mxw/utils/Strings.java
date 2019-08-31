package com.mxw.utils;

import java.util.List;

/**
 * String utility functions.
 *
 * implementation and adapted from <a href="hhttps://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/utils/Strings.java">Web3j</a>
 */
public class Strings {

    private Strings() {}

    public static String toCsv(List<String> src) {
        // return src == null ? null : String.join(", ", src.toArray(new String[0]));
        return join(src, ", ");
    }

    public static String join(List<String> src, String delimiter) {
        return src == null ? null : String.join(delimiter, src.toArray(new String[0]));
    }

    public static String capitaliseFirstLetter(String string) {
        if (string == null || string.length() == 0) {
            return string;
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
    }

    public static String lowercaseFirstLetter(String string) {
        if (string == null || string.length() == 0) {
            return string;
        } else {
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        }
    }

    public static String zeros(int n) {
        return repeat('0', n);
    }

    public static String repeat(char value, int n) {
        return new String(new char[n]).replace("\0", String.valueOf(value));
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String jsonStringCorrection(String value) {
        String json = value.replace("\\\\\\\"","\"").replace("\\\"","\"");
        json = json.replace("\"[","[").replace("]\"","]");
        json = json.replace("\"{","{").replace("}\"","}");
        return json;
    }

    public static boolean jsJsonString(String value) {
        return (value.startsWith("{") && value.endsWith("}")) ||
                (value.startsWith("[") && value.endsWith("]"));
    }
}
