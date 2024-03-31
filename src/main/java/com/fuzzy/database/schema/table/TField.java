package com.fuzzy.database.schema.table;

import com.fuzzy.database.schema.table.TableReference;

import java.io.Serializable;
import java.util.Objects;

public class TField {

    private final String name;
    private final Class<? extends Serializable> type;
    private final TableReference foreignTable;

    public TField(String name, Class<? extends Serializable> type) {
        this(name, type, null);
    }

    public TField(String name, TableReference foreignTable) {
        this(name, Long.class, foreignTable);
    }

    private TField(String name, Class<? extends Serializable> type, TableReference foreignTable) {
        this.name = name;
        this.type = type;
        this.foreignTable = foreignTable;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Serializable> getType() {
        return type;
    }

    public TableReference getForeignTable() {
        return foreignTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TField field = (TField) o;
        return Objects.equals(name, field.name) &&
                Objects.equals(type, field.type) &&
                Objects.equals(foreignTable, field.foreignTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, foreignTable);
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", foreignTable='" + foreignTable + '\'' +
                '}';
    }
}
