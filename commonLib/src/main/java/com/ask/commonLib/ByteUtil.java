package com.ask.commonLib;

/**
 * Created by anc on 2019/4/3
 */
public class ByteUtil {
    private static final String TAG = ByteUtil.class.getSimpleName();

    public static boolean isBytesEquals(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null || bytes2 == null || bytes1.length != bytes2.length) return false;
        if (bytes1 == bytes2) return true;
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) return false;
        }
        return true;
    }

    public static byte[] getBytesFromInt(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24));
        bytes[1] = (byte) ((value >> 16));
        bytes[2] = (byte) ((value >> 8));
        bytes[3] = (byte) (value);
        return bytes;
    }

    public static String getStringFromBytes(byte[] value) {
        if (value == null) return null;
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < value.length; i++) {
            builder.append(value[i]);
            builder.append(i == value.length - 1 ? "]" : ", ");
        }
        return builder.toString();
    }

    public static String getHexStringFromBytes(byte[] value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < value.length; i++) {
            String s = Integer.toHexString(value[i] & 0xFF);
            if (s.length() < 2) builder.append(0);
            builder.append(s.toUpperCase());
            builder.append(i == value.length - 1 ? "]" : ", ");
        }
        return builder.toString();
    }

    public static String getStringFromInts(int[] value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < value.length; i++) {
            builder.append(value[i]);
            builder.append(i == value.length - 1 ? "]" : ", ");
        }
        return builder.toString();
    }

    public static int getIntFromBytes(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    private int[] getIntArrayFromBytes(byte[] value) {
        int[] ints = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            ints[i] = value[i] & 0XFF;
        }
        return ints;
    }
}
