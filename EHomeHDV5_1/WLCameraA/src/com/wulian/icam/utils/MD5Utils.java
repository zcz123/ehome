package com.wulian.icam.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/1/19.
 */

public class MD5Utils {
    private static final char[] LowerDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] md5(byte[] in) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(in);
            return e.digest();
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String encrypt(String strIN) {
        byte[] md5bytes = md5(strIN.getBytes());
        return getLowerHEXfromString(md5bytes);
    }

    public static String getLowerHEXfromString(byte[] result) {
        int len = result.length;
        char[] str = new char[len * 2];
        int k = 0;

        for (int i = 0; i < len; ++i) {
            byte byte0 = result[i];
            str[k++] = LowerDigit[byte0 >>> 4 & 15];
            str[k++] = LowerDigit[byte0 & 15];
        }

        return new String(str);
    }


}
