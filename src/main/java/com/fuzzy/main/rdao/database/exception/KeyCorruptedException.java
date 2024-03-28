package com.fuzzy.main.rdao.database.exception;

public class KeyCorruptedException extends RuntimeException {

    public KeyCorruptedException(final byte[] key) {
        super("Key corrupted, key length is " + key.length);
    }

    public KeyCorruptedException(String message) {
        super(message);
    }
}
