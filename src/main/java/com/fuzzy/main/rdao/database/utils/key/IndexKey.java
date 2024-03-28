package com.fuzzy.main.rdao.database.utils.key;

import static com.fuzzy.main.rdao.database.schema.BaseIndex.ATTENDANT_BYTE_SIZE;

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