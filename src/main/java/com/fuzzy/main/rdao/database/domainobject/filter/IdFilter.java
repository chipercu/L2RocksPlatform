package com.fuzzy.main.rdao.database.domainobject.filter;

public class IdFilter implements Filter {

    private final long fromId;
    private final long toId;

    public IdFilter(long fromId, long toId) {
        checkId(fromId);
        checkId(toId);
        if (fromId > toId) {
            throw new IllegalArgumentException("fromId=" + fromId + " greater than toId=" + toId);
        }

        this.fromId = fromId;
        this.toId = toId;
    }

    public IdFilter(long fromId) {
        this(fromId, Long.MAX_VALUE);
    }

    public long getFromId() {
        return fromId;
    }

    public long getToId() {
        return toId;
    }

    private static void checkId(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Id value is negative, " + value);
        }
    }
}
