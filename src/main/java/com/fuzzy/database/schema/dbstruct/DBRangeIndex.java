package com.fuzzy.database.schema.dbstruct;

import com.fuzzy.database.exception.IllegalTypeException;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.schema.dbstruct.JsonUtils;
import com.fuzzy.database.utils.IndexUtils;
import com.fuzzy.database.utils.TypeConvert;
import net.minidev.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class DBRangeIndex extends DBBaseIntervalIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("rng");
    private static final String JSON_PROP_BEGIN_FIELD_ID = "begin_field_id";
    private static final String JSON_PROP_END_FIELD_ID = "end_field_id";
    private static final String JSON_PROP_HASH_FIELD_IDS = "hash_field_ids";

    private final int beginFieldId;
    private final int endFieldId;

    DBRangeIndex(int id, com.fuzzy.database.schema.dbstruct.DBField beginField, com.fuzzy.database.schema.dbstruct.DBField endField, com.fuzzy.database.schema.dbstruct.DBField[] hashFields) {
        super(id, concatenate(beginField, endField, hashFields), Arrays.stream(hashFields).mapToInt(com.fuzzy.database.schema.dbstruct.DBField::getId).toArray());
        checkSorting(hashFields);

        this.beginFieldId = beginField.getId();
        this.endFieldId = endField.getId();
    }

    public DBRangeIndex(com.fuzzy.database.schema.dbstruct.DBField beginField, com.fuzzy.database.schema.dbstruct.DBField endField, com.fuzzy.database.schema.dbstruct.DBField[] hashFields) {
        this(-1, beginField, endField, hashFields);
    }

    public int getBeginFieldId() {
        return beginFieldId;
    }

    public int getEndFieldId() {
        return endFieldId;
    }

    @Override
    protected byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }

    static DBRangeIndex fromJson(JSONObject source, List<com.fuzzy.database.schema.dbstruct.DBField> tableFields) throws SchemaException {
        return new DBRangeIndex(
                com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_BEGIN_FIELD_ID, Integer.class, source)),
                IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_END_FIELD_ID, Integer.class, source)),
                IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getIntArrayValue(JSON_PROP_HASH_FIELD_IDS, source))
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_BEGIN_FIELD_ID, beginFieldId);
        object.put(JSON_PROP_END_FIELD_ID, endFieldId);
        object.put(JSON_PROP_HASH_FIELD_IDS, com.fuzzy.database.schema.dbstruct.JsonUtils.toJsonArray(getHashFieldIds()));
        return object;
    }

    @Override
    public Class<?> checkIndexedFieldType(Class<?> expectedType, DBTable table) {
        com.fuzzy.database.schema.dbstruct.DBField field = table.getField(beginFieldId);
        if (field.getType() != expectedType) {
            throw new IllegalTypeException(field.getType(), expectedType);
        }
        return null;
    }

    private static com.fuzzy.database.schema.dbstruct.DBField[] concatenate(com.fuzzy.database.schema.dbstruct.DBField beginFieldId, com.fuzzy.database.schema.dbstruct.DBField endFieldId, com.fuzzy.database.schema.dbstruct.DBField[] hashFieldIds) {
        DBField[] fieldIds = Arrays.copyOf(hashFieldIds, hashFieldIds.length + 2);
        fieldIds[fieldIds.length - 2] = beginFieldId;
        fieldIds[fieldIds.length - 1] = endFieldId;
        return fieldIds;
    }
}
