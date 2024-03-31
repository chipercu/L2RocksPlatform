package com.fuzzy.database.schema.dbstruct;

import com.fuzzy.database.exception.IllegalTypeException;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBObject;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.schema.dbstruct.JsonUtils;
import com.fuzzy.database.utils.IndexUtils;
import com.fuzzy.database.utils.TypeConvert;
import net.minidev.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class DBIntervalIndex extends DBBaseIntervalIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("int");
    private static final String JSON_PROP_INDEXED_FIELD_ID = "indexed_field_id";
    private static final String JSON_PROP_HASH_FIELD_IDS = "hash_field_ids";

    private final int indexedFieldId;

    DBIntervalIndex(int id, com.fuzzy.database.schema.dbstruct.DBField indexedFieldId, com.fuzzy.database.schema.dbstruct.DBField[] hashFieldIds) {
        super(id, concatenate(indexedFieldId, hashFieldIds), Arrays.stream(hashFieldIds).mapToInt(DBObject::getId).toArray());
        checkSorting(hashFieldIds);

        this.indexedFieldId = indexedFieldId.getId();
    }

    public DBIntervalIndex(com.fuzzy.database.schema.dbstruct.DBField indexedFieldId, com.fuzzy.database.schema.dbstruct.DBField[] hashFieldIds) {
        this(-1, indexedFieldId, hashFieldIds);
    }

    public int getIndexedFieldId() {
        return indexedFieldId;
    }


    @Override
    protected byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }

    static DBIntervalIndex fromJson(JSONObject source, List<com.fuzzy.database.schema.dbstruct.DBField> tableFields) throws SchemaException {
        return new DBIntervalIndex(
                com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_INDEXED_FIELD_ID, Integer.class, source)),
                IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getIntArrayValue(JSON_PROP_HASH_FIELD_IDS, source))
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_INDEXED_FIELD_ID, indexedFieldId);
        object.put(JSON_PROP_HASH_FIELD_IDS, com.fuzzy.database.schema.dbstruct.JsonUtils.toJsonArray(getHashFieldIds()));
        return object;
    }

    private static com.fuzzy.database.schema.dbstruct.DBField[] concatenate(com.fuzzy.database.schema.dbstruct.DBField indexedFieldId, com.fuzzy.database.schema.dbstruct.DBField[] hashFieldIds) {
        com.fuzzy.database.schema.dbstruct.DBField[] fieldIds = Arrays.copyOf(hashFieldIds, hashFieldIds.length + 1);
        fieldIds[fieldIds.length - 1] = indexedFieldId;
        return fieldIds;
    }

    @Override
    public Class<?> checkIndexedFieldType(Class<?> expectedType, DBTable table) {
        DBField field = table.getField(indexedFieldId);
        if (field.getType() != expectedType) {
            throw new IllegalTypeException(field.getType(), expectedType);
        }
        return null;
    }
}
