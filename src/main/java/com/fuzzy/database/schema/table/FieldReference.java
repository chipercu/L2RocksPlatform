package com.fuzzy.database.schema.table;

import com.fuzzy.database.schema.dbstruct.DBHashIndex;

import java.util.Objects;

public class FieldReference {

    private final String name;
    private final String namespace;
    private final DBHashIndex hashIndex;

    public FieldReference(String name, String namespace, DBHashIndex hashIndex) {
        this.name = name;
        this.namespace = namespace;
        this.hashIndex = hashIndex;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public DBHashIndex getHashIndex() {
        return hashIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldReference that = (FieldReference) o;
        return name.equals(that.name) &&
                namespace.equals(that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace);
    }

    @Override
    public String toString() {
        return "FieldReference{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", hashIndex=" + hashIndex +
                '}';
    }
}
