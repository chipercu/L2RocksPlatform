package com.fuzzy.database.utils;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;

public class ByteInterval {

    private byte[] begin;
    private byte[] end;

    public byte[] getBegin() {
        return begin;
    }

    public void setBegin(byte[] begin) {
        this.begin = begin;
    }

    public byte[] getEnd() {
        return end;
    }

    public void setEnd(byte[] end) {
        this.end = end;
    }

    public void setBeginIfAbsent(byte[] begin) {
        if (this.begin == null) {
            this.begin = begin;
        }
    }

    public void validate() {
        if (begin != null && end != null && UnsignedBytes.lexicographicalComparator().compare(begin, end) > 0) {
            throw new IllegalArgumentException("Begin interval bigger than end. Begin: " + Bytes.asList(begin) + ", End: " + Bytes.asList(end));
        }
    }
}
