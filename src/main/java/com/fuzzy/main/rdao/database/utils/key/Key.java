package com.fuzzy.main.rdao.database.utils.key;

public abstract class Key {

    public static final int ID_BYTE_SIZE = Long.BYTES;

    private long id;

    public Key(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public abstract byte[] pack();
}
