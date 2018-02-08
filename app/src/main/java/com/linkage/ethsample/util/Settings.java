package com.linkage.ethsample.util;

/**
 * Created by Administrator on 2018\2\7 0007.
 */

public class Settings {

    public static boolean walletBeingGenerated = false;

    /**
     * <p>charToByte</p>
     * @description 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * <p><hexString2Bytes/p>
     * @description 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        hex = hex.toUpperCase();
        int len = hex.length() / 2;
        byte[] b = new byte[len];
        char[] hc = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int p = 2 * i;
            b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
        }
        return b;
    }
}
