package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.exception.IllegalTypeException;
import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.utils.IndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
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

    DBRangeIndex(int id, DBField beginField, DBField endField, DBField[] hashFields) {
        super(id, concatenate(beginField, endField, hashFields), Arrays.stream(hashFields).mapToInt(DBField::getId).toArray());
        checkSorting(hashFields);

        this.beginFieldId = beginField.getId();
        this.endFieldId = endField.getId();
    }

    public DBRangeIndex(DBField beginField, DBField endField, DBField[] hashFields) {
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

    static DBRangeIndex fromJson(JSONObject source, List<DBField> tableFields) throws SchemaException {
        return new DBRangeIndex(
                JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                IndexUtils.getFieldsByIds(tableFields, JsonUtils.getValue(JSON_PROP_BEGIN_FIELD_ID, Integer.class, source)),
                IndexUtils.getFieldsByIds(tableFields, JsonUtils.getValue(JSON_PROP_END_FIELD_ID, Integer.class, source)),
                IndexUtils.getFieldsByIds(tableFields, JsonUtils.getIntArrayValue(JSON_PROP_HASH_FIELD_IDS, source))
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_BEGIN_FIELD_ID, beginFieldId);
        object.put(JSON_PROP_END_FIELD_ID, endFieldId);
        object.put(JSON_PROP_HASH_FIELD_IDS, JsonUtils.toJsonArray(getHashFieldIds()));
        return object;
    }

    @Override
    public Class<?> checkIndexedFieldType(Class<?> expectedType, DBTable table) {
        DBField field = table.getField(beginFieldId);
        if (field.getType() != expectedType) {
            throw new IllegalTypeException(field.getType(), expectedType);
        }
        return null;
    }

    private static DBField[] concatenate(DBField beginFieldId, DBField endFieldId, DBField[] hashFieldIds) {
        DBField[] fieldIds = Arrays.copyOf(hashFieldIds, hashFieldIds.length + 2);
        fieldIds[fieldIds.length - 2] = beginFieldId;
        fieldIds[fieldIds.length - 1] = endFieldId;
        return fieldIds;
    }
}
