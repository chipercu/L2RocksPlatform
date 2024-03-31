package com.fuzzy.database.schema.table;

import com.fuzzy.database.schema.table.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Table {

    private final String name;
    private final String namespace;
    private final List<TField> fields;
    private final List<THashIndex> hashIndexes;
    private final List<TPrefixIndex> prefixIndexes;
    private final List<TIntervalIndex> intervalIndexes;
    private final List<TRangeIndex> rangeIndexes;

    public Table(String name, String namespace, List<TField> fields) {
        this(name, namespace, fields, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Table(String name, String namespace, List<TField> fields, List<THashIndex> hashIndexes) {
        this(name, namespace, fields, hashIndexes, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Table(String name, String namespace, List<TField> fields,
                 List<THashIndex> hashIndexes, List<TPrefixIndex> prefixIndexes, List<TIntervalIndex> intervalIndexes, List<TRangeIndex> rangeIndexes) {
        this.name = name;
        this.namespace = namespace;
        this.fields = fields;
        this.hashIndexes = hashIndexes;
        this.prefixIndexes = prefixIndexes;
        this.intervalIndexes = intervalIndexes;
        this.rangeIndexes = rangeIndexes;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<TField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public List<THashIndex> getHashIndexes() {
        return Collections.unmodifiableList(hashIndexes);
    }

    public List<TPrefixIndex> getPrefixIndexes() {
        return Collections.unmodifiableList(prefixIndexes);
    }

    public List<TIntervalIndex> getIntervalIndexes() {
        return Collections.unmodifiableList(intervalIndexes);
    }

    public List<TRangeIndex> getRangeIndexes() {
        return Collections.unmodifiableList(rangeIndexes);
    }

    public boolean same(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(name, table.name) &&
                Objects.equals(namespace, table.namespace) &&
                (fields.containsAll(table.fields) && fields.size() == table.fields.size()) &&
                (Objects.isNull(hashIndexes) ? table.hashIndexes == null : hashIndexes.containsAll(table.hashIndexes)) &&
                (Objects.isNull(prefixIndexes) ? table.prefixIndexes == null : prefixIndexes.containsAll(table.prefixIndexes)) &&
                (Objects.isNull(intervalIndexes) ? table.intervalIndexes == null : intervalIndexes.containsAll(table.intervalIndexes)) &&
                (Objects.isNull(rangeIndexes) ? table.rangeIndexes == null : rangeIndexes.containsAll(table.rangeIndexes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(name, table.name) &&
                Objects.equals(namespace, table.namespace) &&
                Objects.equals(fields, table.fields) &&
                Objects.equals(hashIndexes, table.hashIndexes) &&
                Objects.equals(prefixIndexes, table.prefixIndexes) &&
                Objects.equals(intervalIndexes, table.intervalIndexes) &&
                Objects.equals(rangeIndexes, table.rangeIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, fields, hashIndexes, prefixIndexes, intervalIndexes, rangeIndexes);
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                "namespace='" + namespace + '\'' +
                ", fields=" + fields +
                ", hashIndexes=" + hashIndexes +
                ", prefixIndexes=" + prefixIndexes +
                ", intervalIndexes=" + intervalIndexes +
                ", rangeIndexes=" + rangeIndexes +
                '}';
    }
}
