package com.fuzzy.database.schema.dbstruct;

import net.minidev.json.JSONObject;

public abstract class DBObject {

    static final String JSON_PROP_ID = "id";

    private int id;

    DBObject(int id) {
        this.id = id;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    abstract JSONObject toJson();
}
