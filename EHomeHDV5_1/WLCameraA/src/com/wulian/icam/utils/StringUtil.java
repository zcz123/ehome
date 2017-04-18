package com.wulian.icam.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/21.
 */

public class StringUtil {
    public StringUtil() {
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        } else {
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; ++i) {
                        if (a.charAt(i) != b.charAt(i)) {
                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            str = str.trim();
            return "".equals(str) || "null".equals(str);
        }
    }

    public static boolean isMessyCode(String str) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9/+:,]*$");
        Matcher isMessyCode = pattern.matcher(str);
        return !isMessyCode.matches();
    }

    public static String appendLeft(String src, int len, char c) {
        StringBuffer sb = new StringBuffer();
        int srcLen = src.length();
        if (srcLen >= len) {
            sb.append(src);
        } else {
            int appendSize = len - srcLen;

            for (int i = appendSize; i > 0; --i) {
                sb.append(c);
            }

            sb.append(src);
        }

        return sb.toString();
    }

    public static Integer toInteger(String str) {
        Integer i = Integer.valueOf(-1);

        try {
            i = Integer.valueOf(Integer.parseInt(str));
        } catch (NumberFormatException var3) {
            i = Integer.valueOf(-1);
        }

        return i;
    }

    public static Integer toInteger(Object obj) {
        String str = String.valueOf(obj);
        return toInteger(str);
    }

    public static long toLong(String str) {
        long l = 0L;

        try {
            l = Long.parseLong(str);
        } catch (NumberFormatException var4) {
            l = 0L;
        }

        return l;
    }

    public static long toLong(Object obj) {
        String str = String.valueOf(obj);
        return toLong(str);
    }

    public static Float toFloat(String str) {
        Float f = Float.valueOf(0.0F);

        try {
            f = Float.valueOf(Float.parseFloat(str));
        } catch (NumberFormatException var3) {
            f = Float.valueOf(0.0F);
        }

        return f;
    }

    public static Float toFloat(Object obj) {
        String str = String.valueOf(obj);
        return toFloat(str);
    }

    public static Double toDouble(String str) {
        Double d = Double.valueOf(0.0D);

        try {
            d = Double.valueOf(Double.parseDouble(str));
        } catch (NumberFormatException var3) {
            d = Double.valueOf(0.0D);
        }

        return d;
    }

    public static Double toDouble(Object obj) {
        String str = String.valueOf(obj);
        return toDouble(str);
    }

    public static String toHexString(int i, int len) {
        String str = Integer.toHexString(i);
        return appendLeft(str, len, '0');
    }

    public static Integer toInteger(String s, int radix) {
        Integer i = Integer.valueOf(0);
        if (isNullOrEmpty(s)) {
            return i;
        } else {
            try {
                i = Integer.valueOf(Integer.parseInt(s, radix));
            } catch (NumberFormatException var4) {
                i = Integer.valueOf(0);
            }

            return i;
        }
    }

    public static String format(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch (Exception var3) {
            return format;
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            String hexString = "";

            for (int i = 0; i < bytes.length; ++i) {
                String hex = Integer.toHexString(bytes[i] & 255);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }

                hexString = hexString + hex.toUpperCase();
            }

            return hexString;
        } else {
            return null;
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString != null && !"".equals(hexString)) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] bytes = new byte[length];

            for (int i = 0; i < length; ++i) {
                bytes[i] = (byte) (charToByte(hexChars[i * 2]) << 4 | charToByte(hexChars[i * 2 + 1]));
            }

            return bytes;
        } else {
            return null;
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String stringToHexString(String s) {
        StringBuilder hexSb = new StringBuilder("");
        int sLen = s.length();

        for (int i = 0; i < sLen; ++i) {
            char ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            hexSb.append(s4);
        }

        return hexSb.toString();
    }

    public static String hexStringToString(String hexString) {
        byte[] byteArr = new byte[hexString.length() / 2];
        int byteArrLen = byteArr.length;

        for (int e1 = 0; e1 < byteArrLen; ++e1) {
            try {
                byteArr[e1] = (byte) (255 & Integer.parseInt(hexString.substring(e1 * 2, e1 * 2 + 2), 16));
            } catch (Exception var6) {
//                Logger.error(var6);
            }
        }

        try {
            hexString = new String(byteArr, "utf-8");
        } catch (Exception var5) {
//            Logger.error(var5);
        }

        return hexString;
    }

    public static String transposeHighLow(String cStr) {
        if (cStr != null && !"".equals(cStr)) {
            StringBuffer javaSB = new StringBuffer();
            String highStr = "";
            String lowStr = "";

            while (cStr.length() >= 4) {
                highStr = cStr.substring(0, 2);
                lowStr = cStr.substring(2, 4);
                cStr = cStr.substring(4);
                javaSB.append(lowStr);
                javaSB.append(highStr);
            }

            return javaSB.toString();
        } else {
            return cStr;
        }
    }

    public static boolean isASCII(byte[] bytes) {
        if (bytes == null) {
            return false;
        } else {
            int len = bytes.length;

            for (int i = 0; i < len; ++i) {
                if (bytes[i] > 127 || bytes[i] < 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String objToString(Object obj) {
        String str = "";
        if (obj != null) {
            str = obj.toString();
        }

        return str;
    }

    public static String getShortString(String src, int toLength) {
        String result = "";
        if (src == null) {
            return result;
        } else {
            if (src.length() < toLength) {
                result = src;
            } else {
                result = src.substring(0, toLength) + "...";
            }

            return result;
        }
    }

    public static String getStringEscapeEmpty(String src) {
        String result = "";
        return src == null ? result : (src.equals("null") ? result : src);
    }

    public static String toDecimalString(int i, int len) {
        String str = Integer.toString(i);
        return appendLeft(str, len, '0');
    }

    public static String getStringUTF8(String str) {
        String result = "";
        if (!isNullOrEmpty(str)) {
            try {
                result = new String(hexStringToBytes(str), "UTF-8");
            } catch (UnsupportedEncodingException var3) {
//                Logger.error(var3);
            }
        }

        return result;
    }

    public static String getIpFromString(String ipStr) {
        String pattern = "(\\d{1,3}\\.){3}\\d{1,3}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(ipStr);
        String result = null;

        while (m.find()) {
            result = m.group();
            if (!isNullOrEmpty(result)) {
                break;
            }
        }

        if (isNullOrEmpty(result)) {
            return null;
        } else {
            boolean isOK = true;
            String[] array = result.split("\\.");

            for (int i = 0; i < array.length; ++i) {
                int ip = Integer.parseInt(array[i]);
                if (0 > ip || ip > 255) {
                    isOK = false;
                    break;
                }
            }

            return isOK ? result : null;
        }
    }

    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp = false;

        try {
            long ipNum = getIpNum(ipAddress);
            long aBegin = getIpNum("10.0.0.0");
            long aEnd = getIpNum("10.255.255.255");
            long bBegin = getIpNum("172.16.0.0");
            long bEnd = getIpNum("172.31.255.255");
            long cBegin = getIpNum("192.168.0.0");
            long cEnd = getIpNum("192.168.255.255");
            isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals("127.0.0.1");
        } catch (Exception var16) {
            ;
        }

        return isInnerIp;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return userIp >= begin && userIp <= end;
    }

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = (long) Integer.parseInt(ip[0]);
        long b = (long) Integer.parseInt(ip[1]);
        long c = (long) Integer.parseInt(ip[2]);
        long d = (long) Integer.parseInt(ip[3]);
        long ipNum = a * 256L * 256L * 256L + b * 256L * 256L + c * 256L + d;
        return ipNum;
    }
}
