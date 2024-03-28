package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.utils.IndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import net.minidev.json.JSONObject;

import java.util.List;

public class DBPrefixIndex extends DBIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("prf");
    private static final String JSON_PROP_FIELD_IDS = "field_ids";

    DBPrefixIndex(int id, DBField[] fields) {
        super(id, fields);
    }

    public DBPrefixIndex(DBField[] fields) {
        this(-1, fields);
    }

    static DBPrefixIndex fromJson(JSONObject source, List<DBField> tableFields) throws SchemaException {
        return new DBPrefixIndex(
                JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                IndexUtils.getFieldsByIds(tableFields, JsonUtils.getIntArrayValue(JSON_PROP_FIELD_IDS, source))
        );
    }

    @Override
    protected byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_FIELD_IDS, JsonUtils.toJsonArray(getFieldIds()));
        return object;
    }
}
