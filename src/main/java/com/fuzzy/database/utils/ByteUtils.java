package com.fuzzy.database.utils;

public class ByteUtils {

    public static boolean startsWith(byte[] prefix, byte[] source) {
        return startsWith(prefix, 0, prefix.length, source);
    }

    public static boolean startsWith(byte[] prefix, int offset, int len, byte[] source) {
        if (len > (source.length - offset)) {
            return false;
        }

        for (int i = 0; i < len; ++i, ++offset) {
            if (source[offset] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(byte[] left, int lfrom, int lto, byte[] right, int rfrom, int rto) {
        if ((lto - lfrom) != (rto - rfrom)) {
            return false;
        }

        for (; lfrom < lto; ++lfrom, ++rfrom) {
            if (left[lfrom] != right[rfrom]) {
                return false;
            }
        }
        return true;
    }

    public static boolean endsWith(byte[] suffix, byte[] source) {
        if (suffix.length > source.length) {
            return false;
        }

        int j = source.length - suffix.length;
        for (int i = 0; i < suffix.length; ++i, ++j) {
            if (source[j] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(byte[] value) {
        return value == null || value.length == 0;
    }

    public static int indexOf(byte value, byte[] source) {
        for (int i = 0; i < source.length; ++i) {
            if (value == source[i]) {
                return i;
            }
        }
        return -1;
    }
}
