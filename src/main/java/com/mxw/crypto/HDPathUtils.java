package com.mxw.crypto;

import java.math.BigInteger;

import static com.mxw.crypto.Bip32ECKeyPair.HARDENED_BIT;

public class HDPathUtils {

    private static final String PREFIX_PRIVATE = "m";
    private static final String PREFIX_PUBLIC = "M";
    private static final String SEPARATOR = "/";


    public static int[] fromStringPath(String path) {
        if (!path.startsWith(PREFIX_PRIVATE) && !path.startsWith(PREFIX_PUBLIC)) {
            //参数非法
            return null;
        }
        String[] pathArray = path.split(SEPARATOR);
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }
        int[] paths = new int[5];
        for (int i = 1; i < pathArray.length; i++) {
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                paths[i-1] = number;
            } else {
                int number = Integer.parseInt(pathArray[i]);
                paths[i-1] = number;
            }
        }
        return parseHardened(paths);
    }

    public static int[] parseBit(int[] path) {
        int[] numberPath = new int[path.length];
        for (int i=0;i< path.length;i++) {
            BigInteger data = new BigInteger("2147483648");
            if(i<=2){
                numberPath[i] = data.add(new BigInteger(String.valueOf(path[i]))).intValue();
            }else {
                numberPath[i] = path[i];
            }
        }
        return numberPath;
    }

    public static int[] parseHardened(int[] path) {
        int[] numberPath = new int[path.length];
        for (int i=0;i< path.length;i++) {

            if(i<=2){
                numberPath[i] = path[i] | HARDENED_BIT;
            }else {
                numberPath[i] = path[i];
            }
        }
        return numberPath;
    }

    public static String toStringPath(int[] path) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(PREFIX_PRIVATE).append(SEPARATOR);
        int[] bits = parseBit(path);
        for(int i=0;i< bits.length;i++){
            if(i<=2) {
                pathBuilder.append(bits[i]).append("'").append(SEPARATOR);
            }else {
                pathBuilder.append(bits[i]).append(SEPARATOR);
            }
        }
        String hdPath = pathBuilder.toString();
        return hdPath.substring(0, hdPath.length() - 1);
    }

}
