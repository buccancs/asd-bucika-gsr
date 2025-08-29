package com.guide.zm04c.matrix.utils;

/**
 * Backward compatibility wrapper for HexDump functionality.
 * All methods delegate to the shared implementation in com.topdon.lib.core.utils.HexDump.
 * 
 * @deprecated Use com.topdon.lib.core.utils.HexDump directly for new code.
 */
public class HexDump {
    
    public static String dumpHexString(byte[] array) {
        return com.topdon.lib.core.utils.HexDump.dumpHexString(array);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        return com.topdon.lib.core.utils.HexDump.dumpHexString(array, offset, length);
    }

    public static String toHexString(byte b) {
        return com.topdon.lib.core.utils.HexDump.toHexString(b);
    }

    public static String toHexString(byte[] array) {
        return com.topdon.lib.core.utils.HexDump.toHexString(array);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        return com.topdon.lib.core.utils.HexDump.toHexString(array, offset, length);
    }

    public static String toHexString(int i) {
        return com.topdon.lib.core.utils.HexDump.toHexString(i);
    }

    public static byte[] toByteArray(byte b) {
        return com.topdon.lib.core.utils.HexDump.toByteArray(b);
    }

    public static byte[] toByteArray(int i) {
        return com.topdon.lib.core.utils.HexDump.toByteArray(i);
    }

    public static byte[] hexStringToByteArray(String hexString) {
        return com.topdon.lib.core.utils.HexDump.hexStringToByteArray(hexString);
    }

    public static String toHexString(long value) {
        return com.topdon.lib.core.utils.HexDump.toHexString(value);
    }

    public static String toHexString(float f) {
        return com.topdon.lib.core.utils.HexDump.toHexString(f);
    }

    public static String toHexString(double d) {
        return com.topdon.lib.core.utils.HexDump.toHexString(d);
    }

    public static int bytesToInt(byte[] src, int offset) {
        return com.topdon.lib.core.utils.HexDump.bytesToInt(src, offset);
    }

    public static byte[] intToBytes2(int value) {
        return com.topdon.lib.core.utils.HexDump.intToBytes2(value);
    }

    public static void float2byte(float num, byte[] numbyte) {
        com.topdon.lib.core.utils.HexDump.float2byte(num, numbyte);
    }
}