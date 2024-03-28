package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.FieldAlreadyExistsException;
import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.exception.TableNotFoundException;
import com.fuzzy.main.rdao.database.schema.table.FieldReference;
import com.fuzzy.main.rdao.database.schema.table.TableReference;
import com.fuzzy.main.rdao.database.utils.SchemaTableCache;
import net.minidev.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBSchema {

    private final String version;
    private final List<DBTable> tables;

    private final SchemaTableCache schemaTableCache;

    private DBSchema(String version, List<DBTable> tables) {
        this.version = version;
        this.tables = tables;
        this.schemaTableCache = new SchemaTableCache(tables, this);
    }

    public String getVersion() {
        return version;
    }

    public List<DBTable> getTables() {
        return tables;
    }

    public DBTable newTable(String name, String namespace, List<DBField> columns) {
        DBTable dbTable = new DBTable(nextId(tables), name, namespace, columns);
        tables.add(dbTable);
        schemaTableCache.newTable(dbTable);
        return dbTable;
    }

    public void dropTable(String name, String namespace) {
        schemaTableCache.removeTable(name, namespace);
    }

    public int findTableIndex(String tableName, String tableNamespace) throws SchemaException {
        DBTable dbTable;
        for (int i = 0; i < tables.size(); ++i) {
            dbTable = tables.get(i);
            if (dbTable.getName().equals(tableName) && dbTable.getNamespace().equals(tableNamespace)) {
                return i;
            }
        }
        return -1;
    }

    public DBTable getTableById(int id) throws SchemaException {
        return tables.stream().filter(table ->  table.getId() == id)
                .findAny()
                .orElseThrow(() -> new TableNotFoundException("Table with id: " + id + " doesn't found"));
    }

    public List<DBTable> getTablesByNamespace(String namespace) throws SchemaException {
        return tables.stream().filter(table ->  table.getNamespace().equals(namespace)).collect(Collectors.toList());
    }

    public DBTable getTable(String name, String namespace) throws SchemaException {
        DBTable table = schemaTableCache.getTable(name, namespace);
        if (table == null) {
            throw new TableNotFoundException(namespace + "." + name);
        }
        return table;
    }

    public DBField createField(String fieldName,
                                Class<? extends Serializable> fieldType,
                                TableReference fieldForeignTable,
                                String tableName,
                                String tableNamespace) throws DatabaseException {
        DBTable dbTable = getTable(tableName, tableNamespace);
        int i = dbTable.findFieldIndex(fieldName);
        if (i != -1) {
            throw new FieldAlreadyExistsException(fieldName, dbTable.getName(), dbTable.getNamespace());
        }

        Integer fTableId = fieldForeignTable != null
                ? getTable(fieldForeignTable.getName(), fieldForeignTable.getNamespace()).getId()
                : null;
        DBField dbField = dbTable.newField(fieldName, fieldType, fTableId);
        schemaTableCache.createField(dbField, dbTable);
        return dbField;
    }

    public void dropField(String fieldName, String tableName, String namespace) throws DatabaseException {
        DBTable table = getTable(tableName, namespace);
        DBField field = table.getField(fieldName);
        schemaTableCache.removeField(field, table);
    }

    public void checkIntegrity() throws SchemaException {
        checkUniqueId(tables);

        for (DBTable table : tables) {
            table.checkIntegrity();
        }
    }

    public static DBSchema fromStrings(String version, String tablesJson) throws SchemaException {
        return new DBSchema(version, JsonUtils.toList(JsonUtils.parse(tablesJson, JSONArray.class), DBTable::fromJson));
    }

    public String toTablesJsonString() {
        return JsonUtils.toJsonArray(tables).toJSONString();
    }

    public Set<FieldReference> getTableReferences(String tableName, String namespace) {
        return schemaTableCache.getTableReference(tableName, namespace);
    }

    static int nextId(List<? extends DBObject> items) {
        return nextId(items.stream());
    }

    static int nextId(Stream<? extends DBObject> items) {
        return items
                .map(DBObject::getId)
                .max(Integer::compare)
                .orElse(-1) + 1;
    }

    static <T extends DBObject> void checkUniqueId(List<T> objects) throws SchemaException {
        for (int i = 0; i < objects.size(); ++i) {
            T obj = objects.get(i);

            for (int j = i + 1; j < objects.size(); ++j) {
                if (objects.get(j).getId() == obj.getId()) {
                    throw new SchemaException("Not unique id=" + obj.getId() + " for " + obj.getClass().getSimpleName());
                }
            }
        }
    }
}
