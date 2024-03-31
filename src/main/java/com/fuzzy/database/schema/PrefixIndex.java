package com.fuzzy.database.schema;

import com.fuzzy.database.schema.BaseIndex;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.utils.TypeConvert;

import java.util.Collection;

public class PrefixIndex extends BaseIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("prf");

    PrefixIndex(com.fuzzy.database.anotation.PrefixIndex index, StructEntity parent) {
        super(buildIndexedFields(index.fields(), parent), parent);
    }

    public static String toString(Collection<String> indexedFields) {
        return PrefixIndex.class.getSimpleName() + ": " + indexedFields;
    }

    @Override
    public byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }
}
