package com.fuzzy.database.utils.key;

import com.fuzzy.database.utils.key.Key;

import static com.fuzzy.database.schema.BaseIndex.ATTENDANT_BYTE_SIZE;

abstract class IndexKey extends Key {

    final byte[] attendant;

    IndexKey(long id, byte[] attendant) {
        super(id);
        checkAttendant(attendant);
        this.attendant = attendant;
    }

    private static void checkAttendant(final byte[] attendant) {
        if(attendant == null || attendant.length != ATTENDANT_BYTE_SIZE) {
            throw new IllegalArgumentException();
        }
    }
}