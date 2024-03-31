package com.fuzzy.database.schema;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectSource;
import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.iterator.IteratorEntity;
import com.fuzzy.database.exception.*;
import com.fuzzy.database.provider.*;
import com.fuzzy.database.schema.*;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.dbstruct.*;
import com.fuzzy.database.schema.table.*;
import com.fuzzy.database.utils.IndexService;
import com.fuzzy.database.utils.TableUtils;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.database.utils.key.FieldKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Все методы по изменению схемы не транзакционны.
 * Если возникнет ошибка при изменении схемы, то объектное описание схемы в памяти,
 * ее описание на диске и структура данных будут несогласованны
 */
public class Schema {

    private final static Logger log = LoggerFactory.getLogger(Schema.class);

    public static Set<Class<? extends Serializable>> SUPPORTED_FIELD_TYPES = new HashSet<>(Arrays.asList(
            String.class, byte[].class,
            Long.class, Integer.class, Boolean.class, Double.class,
            Instant.class, LocalDateTime.class
    ));

    static final String CURRENT_VERSION = "1.0.0";

    public static final String SERVICE_COLUMN_FAMILY = "service";
    static final byte[] VERSION_KEY = TypeConvert.pack("version");
    static final byte[] SCHEMA_KEY = TypeConvert.pack("schema");
    private static DBSchema cacheDbSchema;

    private final DBProvider dbProvider;
    private final DBSchema dbSchema;
    private final static ConcurrentMap<Class<? extends DomainObject>, StructEntity> objTables = new ConcurrentHashMap<>();
    private final static ConcurrentMap<TableReference, Class<? extends DomainObject>> tableClasses = new ConcurrentHashMap<>();

    private Schema(DBProvider dbProvider, DBSchema schema) {
        this.dbProvider = dbProvider;
        this.dbSchema = schema;
    }

    public static Schema create(DBProvider dbProvider) throws DatabaseException {
        return new Schema(dbProvider, createSchema(dbProvider));
    }

    public static Schema read(DBProvider dbProvider) throws DatabaseException {
        cacheDbSchema = readSchema(dbProvider);
        return new Schema(dbProvider, cacheDbSchema);
    }

    public static Schema readFromCache(DBProvider dbProvider) throws DatabaseException {
        if (Objects.nonNull(cacheDbSchema)) {
            return new Schema(dbProvider, cacheDbSchema);
        }
        cacheDbSchema = readSchema(dbProvider);
        return new Schema(dbProvider, cacheDbSchema);
    }

    private static DBSchema createSchema(DBProvider dbProvider) throws DatabaseException {
        dbProvider.createColumnFamily(SERVICE_COLUMN_FAMILY);

        String version = TypeConvert.unpackString(dbProvider.getValue(SERVICE_COLUMN_FAMILY, VERSION_KEY));
        String schemaJson = TypeConvert.unpackString(dbProvider.getValue(SERVICE_COLUMN_FAMILY, SCHEMA_KEY));
        if (version != null || schemaJson != null) {
            throw new SchemaException("Schema already exists");
        }

        DBSchema newSchema = DBSchema.fromStrings(CURRENT_VERSION, "[]");
        saveSchema(newSchema, dbProvider);
        return newSchema;
    }

    public static boolean exists(DBProvider dbProvider) throws DatabaseException {
        return dbProvider.containsColumnFamily(SERVICE_COLUMN_FAMILY);
    }

    private static DBSchema readSchema(DBProvider dbProvider) throws DatabaseException {
        String version = TypeConvert.unpackString(dbProvider.getValue(SERVICE_COLUMN_FAMILY, VERSION_KEY));
        String schemaJson = TypeConvert.unpackString(dbProvider.getValue(SERVICE_COLUMN_FAMILY, SCHEMA_KEY));
        validateSchema(version, schemaJson);
        return DBSchema.fromStrings(version, schemaJson);
    }

    private static void validateSchema(String version, String schemaJson) throws DatabaseException {
        if (version == null) {
            if (schemaJson == null) {
                throw new SchemaException("Schema not found");
            }
            throw new CorruptedException("Key 'version' not found");
        } else if (schemaJson == null) {
            throw new CorruptedException("Key 'schema' not found");
        }

        if (!CURRENT_VERSION.equals(version)) {
            throw new SchemaException("Incorrect version of the database (" + version + "). Current version is " + CURRENT_VERSION + ".");
        }
    }

    private static void saveSchema(DBSchema schema, DBProvider dbProvider) throws DatabaseException {
        try (DBTransaction transaction = dbProvider.beginTransaction()) {
            transaction.put(SERVICE_COLUMN_FAMILY, VERSION_KEY, TypeConvert.pack(schema.getVersion()));
            transaction.put(SERVICE_COLUMN_FAMILY, SCHEMA_KEY, TypeConvert.pack(schema.toTablesJsonString()));
            transaction.commit();
        }
    }

    public DBProvider getDbProvider() {
        return dbProvider;
    }

    public DBSchema getDbSchema() {
        return dbSchema;
    }

    @Deprecated
    public boolean existTable(StructEntity table) {
        return dbSchema.findTableIndex(table.getName(), table.getNamespace()) != -1;
    }

    public void createTable(Table table) throws DatabaseException {
        int tableIndex = dbSchema.findTableIndex(table.getName(), table.getNamespace());
        DBTable dbTable;
        if (tableIndex == -1) {
            dbTable = dbSchema.newTable(table.getName(), table.getNamespace(), new ArrayList<>());

            dbProvider.createColumnFamily(dbTable.getDataColumnFamily());
            dbProvider.createColumnFamily(dbTable.getIndexColumnFamily());
            dbProvider.createSequence(dbTable.getDataColumnFamily());
        } else {
            throw new TableAlreadyExistsException(dbSchema.getTables().get(tableIndex));
        }
        for (TField tableField : table.getFields()) {
            createField(tableField, dbTable);
        }
        for (THashIndex index : table.getHashIndexes()) {
            createIndex(index, dbTable);
        }
        for (TPrefixIndex index : table.getPrefixIndexes()) {
            createIndex(index, dbTable);
        }
        for (TIntervalIndex index : table.getIntervalIndexes()) {
            createIndex(index, dbTable);
        }
        for (TRangeIndex index : table.getRangeIndexes()) {
            createIndex(index, dbTable);
        }
        saveSchema();
    }

    private static com.fuzzy.database.schema.Field getStructEntityField(DBField dbFields, DBTable table) {
        final com.fuzzy.database.schema.Field[] structEntityFields = getStructEntity(table).getFields();
        for (com.fuzzy.database.schema.Field structEntityField : structEntityFields) {
            if (structEntityField.getName().equals(dbFields.getName())) {
                return structEntityField;
            }
        }
        throw new SchemaException("Required field:" + dbFields.getName() + "from schema doesn't found in StructEntity");
    }

    public Table getTable(String name, String namespace) {
        DBTable dbTable = dbSchema.getTable(name, namespace);
        return TableUtils.buildTable(dbTable, dbSchema);
    }

    @Deprecated
    public void createTable(StructEntity table) throws DatabaseException {
        Schema.resolve(table.getObjectClass());
        int tableIndex = dbSchema.findTableIndex(table.getName(), table.getNamespace());
        DBTable dbTable;
        if (tableIndex == -1) {
            dbTable = dbSchema.newTable(table.getName(), table.getNamespace(), new ArrayList<>());

            dbProvider.createColumnFamily(dbTable.getDataColumnFamily());
            dbProvider.createColumnFamily(dbTable.getIndexColumnFamily());
            dbProvider.createSequence(dbTable.getDataColumnFamily());
        } else {
            throw new TableAlreadyExistsException(dbSchema.getTables().get(tableIndex));
        }
        for (com.fuzzy.database.schema.Field tableField : table.getFields()) {
            createField(tableField, dbTable, table);
        }
        for (HashIndex index : table.getHashIndexes()) {
            createIndex(index, dbTable, table);
        }
        for (PrefixIndex index : table.getPrefixIndexes()) {
            createIndex(index, dbTable, table);
        }
        for (IntervalIndex index : table.getIntervalIndexes()) {
            createIndex(index, dbTable, table);
        }
        for (RangeIndex index : table.getRangeIndexes()) {
            createIndex(index, dbTable, table);
        }
        saveSchema();
    }

    public boolean dropTable(String name, String namespace) throws DatabaseException {
        return dropTable(name, namespace, ActionMode.VALIDATE);
    }

    public boolean dropTable(String name, String namespace, ActionMode actionMode) throws DatabaseException {
        int i = dbSchema.findTableIndex(name, namespace);
        if (i == -1) {
            return false;
        }

        DBTable table = dbSchema.getTables().remove(i);
        dbSchema.dropTable(name, namespace);
        if (actionMode == ActionMode.VALIDATE && hasDependenceOfOtherTable(table.getId())) {
            throw new TableRemoveException("Can't remove table: " + namespace + "." + name + ", there are dependencies on the table");
        }
        dbProvider.dropColumnFamily(table.getDataColumnFamily());
        dbProvider.dropColumnFamily(table.getIndexColumnFamily());
        dbProvider.dropSequence(table.getDataColumnFamily());

        saveSchema();
        return true;
    }

    public boolean dropTablesByNamespace(String namespace) throws DatabaseException {
        List<DBTable> tables = dbSchema.getTablesByNamespace(namespace);
        for (DBTable table : tables) {
            dropTable(table.getName(), table.getNamespace(), ActionMode.FORCE);
        }
        return true;
    }

    public static StructEntity getEntity(Class<? extends DomainObject> clazz) {
        StructEntity entity = objTables.get(clazz);
        if (entity == null) {
            entity = objTables.get(StructEntity.getAnnotationClass(clazz));
            if (entity == null) {
                throw new SchemaException("StructEntity doesn't initialized: " + clazz);
            }
            objTables.putIfAbsent(clazz, entity);
        }
        return entity;
    }

    public static Class<? extends DomainObject> getTableClass(String name, String namespace) {
        return tableClasses.get(new TableReference(name, namespace));
    }

    public static <T extends DomainObject> StructEntity resolve(Class<T> objClass) throws SchemaException {
        Class<? extends DomainObject> annotationClass = StructEntity.getAnnotationClass(objClass);
        StructEntity entity = objTables.get(annotationClass);
        if (entity == null) {
            entity = new StructEntity(annotationClass);
            objTables.put(annotationClass, entity);
        }
        TableReference tableReference = new TableReference(entity.getName(), entity.getNamespace());
        tableClasses.putIfAbsent(tableReference, annotationClass);
        return entity;
    }

    private static <T extends DomainObject> StructEntity buildObjTable(Class<T> objClass) throws SchemaException {
        return new StructEntity(objClass);
    }

    public Collection<StructEntity> getDomains() {
        return objTables.values();
    }

    public void checkIntegrity() throws DatabaseException {
        dbSchema.checkIntegrity();

        //todo V.Bukharkin add check with cf in db for exclude
        Set<String> processedNames = new HashSet<>();
        for (DBTable table : dbSchema.getTables()) {
            if (processedNames.contains(table.getDataColumnFamily())) {
                throw new InconsistentDatabaseException("Column family " + table.getNamespace() + " into " + table.getName() + " already exists.");
            }
            processedNames.add(table.getNamespace());
            if (!dbProvider.containsColumnFamily(table.getDataColumnFamily())) {
                throw new SchemaException("ColumnFamily '" + table.getDataColumnFamily() + "' not found, table='" + table.getName() + "'");
            }
            if (!dbProvider.containsColumnFamily(table.getIndexColumnFamily())) {
                throw new SchemaException("ColumnFamily '" + table.getIndexColumnFamily() + "' not found, table='" + table.getName() + "'");
            }
            if (!dbProvider.containsSequence(table.getDataColumnFamily())) {
                throw new SequenceNotFoundException(table.getDataColumnFamily());
            }
        }
    }

    public void checkSubsystemIntegrity(Set<StructEntity> domains, String namespace) throws DatabaseException {
        Set<String> domainNames = new HashSet<>(domains.size());
        for (StructEntity domain : domains) {
            domainNames.add(domain.getName());
            Table table = getTable(domain.getName(), domain.getNamespace());
            if (table == null) {
                throw new TableNotFoundException("Table in schema for class " + domain.getObjectClass() + " doesn't exist");
            }
            Table domainTable = DBTableUtils.buildTable(domain);
            if (!domainTable.same(table)) {
                throw new InconsistentTableException("Domain table " + domainTable.getNamespace() + "." + domainTable.getName()
                        + " doesn't equal to schema table. \nDomain table: " + domainTable + "\nSchema table: " + table);
            }
        }
        List<DBTable> schemaTables = dbSchema.getTables()
                .stream()
                .filter(t -> t.getNamespace().equals(namespace))
                .collect(Collectors.toList());
        for (DBTable schemaTable : schemaTables) {
            if (!domainNames.contains(schemaTable.getName())) {
                throw new TableNotFoundException("Domain class " + schemaTable.getNamespace() + "." + schemaTable.getName() + " doesn't exists");
            }
        }
    }

//
//    public void renameTable(String oldName, String newName, String namespace) throws DatabaseException {
//        dbSchema.getTable(oldName, namespace).setName(newName);
//        saveSchema();
//    }

    @Deprecated
    private DBField createField(com.fuzzy.database.schema.Field tableField, DBTable dbTable, StructEntity table) throws DatabaseException {
        int i = dbTable.findFieldIndex(tableField.getName());
        if (i != -1) {
            throw new FieldAlreadyExistsException(tableField.getName(), dbTable.getName(), dbTable.getNamespace());
        }

        Integer fTableId = tableField.getForeignDependency() != null
                ? dbSchema.getTable(tableField.getForeignDependency().getName(), tableField.getForeignDependency().getNamespace()).getId()
                : null;
        DBField newField = dbTable.newField(tableField.getName(), tableField.getType(), fTableId);
        if (newField.isForeignKey()) {
            createIndex(new HashIndex(tableField, table), dbTable, table);
        }
        saveSchema();
        return newField;
    }

    private DBField createField(TField tableField, DBTable dbTable) throws DatabaseException {
        int i = dbTable.findFieldIndex(tableField.getName());
        if (i != -1) {
            throw new FieldAlreadyExistsException(tableField.getName(), dbTable.getName(), dbTable.getNamespace());
        }

        Integer fTableId = tableField.getForeignTable() != null
                ? dbSchema.getTable(tableField.getForeignTable().getName(), tableField.getForeignTable().getNamespace()).getId()
                : null;
        DBField newField = dbTable.newField(tableField.getName(), tableField.getType(), fTableId);
        if (newField.isForeignKey()) {
            createIndex(new THashIndex(tableField.getName()), dbTable);
        }
        saveSchema();
        return newField;
    }

    private static StructEntity getStructEntity(DBTable table) {
        final Class<? extends DomainObject> tableClass = Schema.getTableClass(table.getName(), table.getNamespace());
        return new StructEntity(StructEntity.getAnnotationClass(tableClass));
    }

    public void clearTable(String name, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(name, namespace);

        List<DBTable> dependenceTables = getDependenciesOfOtherTable(table.getId());
        if (!dependenceTables.isEmpty()) {
            for (DBTable dependenceTable : dependenceTables) {
                try (DBIterator it = dbProvider.createIterator(dependenceTable.getDataColumnFamily())) {
                    if (it.seek(null) != null) {
                        throw new TableClearException("Can't clear table: "
                                + namespace + "." + name +
                                ", there are dependencies on the data table. " +
                                ". Clear a dependence table before: "
                                + dependenceTable.getNamespace() + "." + dependenceTable.getName());
                    }
                }
            }
        }

        dbProvider.dropColumnFamily(table.getDataColumnFamily());
        dbProvider.dropColumnFamily(table.getIndexColumnFamily());
        dbProvider.dropSequence(table.getDataColumnFamily());

        dbProvider.createColumnFamily(table.getDataColumnFamily());
        dbProvider.createColumnFamily(table.getIndexColumnFamily());
        dbProvider.createSequence(table.getDataColumnFamily());
    }

    public void dropForeignKey(TField tableField, Table table) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(table.getName(), table.getNamespace());
        dropForeignKey(tableField, dbTable);
    }

    public void dropForeignKey(TField tableField, String tableName, String namespace) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(tableName, namespace);
        dropForeignKey(tableField, dbTable);
    }

    private DBField dropForeignKey(TField tableField, DBTable dbTable) throws DatabaseException {
        final String fieldName = tableField.getName();
        final DBField field = dbTable.getField(fieldName);
        final String tableName = dbTable.getName();
        if (field == null) {
            throw new FieldNotFoundException(fieldName, tableName);
        }
        if (!field.isForeignKey()) {
            throw new ForeignDependencyNotFoundException(fieldName, tableName);
        }
        field.setForeignTableId(null);
        saveSchema();
        return field;
    }

    public void appendForeignKey(TField tableField, Table table) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(table.getName(), table.getNamespace());
        appendForeignKey(tableField, dbTable);
    }

    public void appendForeignKey(TField tableField, String tableName, String namespace) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(tableName, namespace);
        appendForeignKey(tableField, dbTable);
    }

    private DBField appendForeignKey(TField tableField, DBTable dbTable) throws DatabaseException {
        final String fieldName = tableField.getName();
        final DBField field = dbTable.getField(fieldName);
        final String tableName = dbTable.getName();
        if (field == null) {
            throw new FieldNotFoundException(fieldName, tableName);
        }

        final Class<? extends Serializable> type = field.getType();
        if (!type.equals(Long.class)) {
            throw new IllegalTypeException(Long.class, type);
        }

        if (field.isForeignKey()) {
            throw new ForeignDependencyAlreadyExistException(fieldName, tableName);
        }

        final TableReference tableReference = tableField.getForeignTable();
        final DBTable referenceTable = dbSchema.getTable(tableReference.getName(), tableReference.getNamespace());

        if (referenceTable == null) {
            throw new TableNotFoundException(tableReference.getName());
        }

        checkForeignDependencyIntegrity(dbTable, tableField, referenceTable);
        field.setForeignTableId(referenceTable.getId());
        createIndex(new THashIndex(field.getName()), dbTable);
        saveSchema();
        return field;
    }

    private void checkForeignDependencyIntegrity(DBTable dbTable, TField tableField, DBTable referenceTable) {
        final DBField field = dbTable.getField(tableField.getName());
        final Field structEntityField = getStructEntityField(field, dbTable);
        DomainObjectSource domainObjectSource = new DomainObjectSource(dbProvider, true);
        final Class<? extends DomainObject> dbTableClass = Schema.getTableClass(dbTable.getName(), dbTable.getNamespace());
        final Class<? extends DomainObject> referenceTableClass = Schema.getTableClass(referenceTable.getName(), referenceTable.getNamespace());
        try (IteratorEntity<? extends DomainObject> iter = domainObjectSource.find(dbTableClass, EmptyFilter.INSTANCE)) {
            while (iter.hasNext()) {
                final DomainObject domainObject = iter.next();
                final Long referenceId = domainObject.get(structEntityField.getNumber());
                if (referenceId == null) {
                    continue;
                }
                final DomainObject referenceObject = domainObjectSource.get(referenceTableClass, referenceId);
                if (Objects.isNull(referenceObject)) {
                    throw new ForeignDependencyException(domainObject.getId(), dbTable, referenceTable, field, referenceId);
                }
            }
        }
    }

    private DBField insertField(int fieldId, TField tableField, DBTable dbTable) throws DatabaseException {
        int i = dbTable.findFieldIndex(tableField.getName());
        if (i != -1) {
            throw new FieldAlreadyExistsException(tableField.getName(), dbTable.getName(), dbTable.getNamespace());
        }

        Integer fTableId = tableField.getForeignTable() != null
                ? dbSchema.getTable(tableField.getForeignTable().getName(), tableField.getForeignTable().getNamespace()).getId()
                : null;
        DBField newField = dbTable.insertNewField(fieldId, tableField.getName(), tableField.getType(), fTableId);
        if (newField.isForeignKey()) {
            createIndex(new THashIndex(tableField.getName()), dbTable);
        }
        saveSchema();
        return newField;
    }

    public void createField(TField tableField, Table table) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(table.getName(), table.getNamespace());
        createField(tableField, dbTable);
    }

    public void createField(TField tableField, String tableName, String namespace) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(tableName, namespace);
        createField(tableField, dbTable);
    }

    public void insertField(int fieldId, TField tableField, Table table) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(table.getName(), table.getNamespace());
        insertField(fieldId, tableField, dbTable);
    }

    public void insertField(int fieldId, TField tableField, String tableName, String tableNamespace) throws DatabaseException {
        DBTable dbTable = dbSchema.getTable(tableName, tableNamespace);
        insertField(fieldId, tableField, dbTable);
    }

    public boolean dropField(String fieldName, String tableName, String namespace) throws DatabaseException {
//        dbSchema.dropField(fieldName, tableName, namespace);
        DBTable table = dbSchema.getTable(tableName, namespace);
        int i = table.findFieldIndex(fieldName);
        if (i == -1) {
            return false;
        }
        DBField field = table.getSortedFields().get(i);
        List<DBHashIndex> dbHashIndexes = dropIndexesByField(field, table.getHashIndexes(), table);
        List<DBPrefixIndex> dbPrefixIndexes = dropIndexesByField(field, table.getPrefixIndexes(), table);
        List<DBIntervalIndex> dbIntervalIndexes = dropIndexesByField(field, table.getIntervalIndexes(), table);
        List<DBRangeIndex> dbRangeIndexes = dropIndexesByField(field, table.getRangeIndexes(), table);
        dbHashIndexes.forEach(table::dropIndex);
        dbPrefixIndexes.forEach(table::dropIndex);
        dbIntervalIndexes.forEach(table::dropIndex);
        dbRangeIndexes.forEach(table::dropIndex);

        dropFieldData(field, table);
        table.dropField(i);
        saveSchema();
        return true;
    }

    private <T extends DBIndex> List<T> dropIndexesByField(DBField field, List<T> indexes, DBTable table) throws DatabaseException {
        List<T> removedIndexes = new ArrayList<>();
        Iterator<T> it = indexes.iterator();
        while (it.hasNext()) {
            T index = it.next();
            if (index.fieldContains(field.getId())) {
                removedIndexes.add(index);
                dropIndexData(index, table);
                it.remove();
            }
        }
        return removedIndexes;
    }

    private <T extends DBIndex> List<T> dropIndexesByField(DBField field, Stream<T> indexes, DBTable table) throws DatabaseException {
        return dropIndexesByField(field, indexes.collect(Collectors.toList()), table);
    }

    public void renameField(String oldName, String newName, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        if (table.containField(newName)) {
            throw new FieldAlreadyExistsException(newName, tableName, namespace);
        }
        DBField field = table.getField(oldName);
        List<DBHashIndex> droppedHashIndexes = dropIndexesByField(field, table.getHashIndexes(), table);
        List<DBPrefixIndex> droppedPrefixIndexes = dropIndexesByField(field, table.getPrefixIndexes(), table);
        List<DBIntervalIndex> droppedIntervalIndexes = dropIndexesByField(field, table.getIntervalIndexes(), table);
        List<DBRangeIndex> droppedRangeIndexes = dropIndexesByField(field, table.getRangeIndexes(), table);
        for (DBHashIndex index : droppedHashIndexes) {
            String[] fields = Arrays.stream(index.getFieldIds()).mapToObj(table::getField).map(DBField::getName).toArray(String[]::new);
            createIndex(new THashIndex(fields), tableName, namespace);
        }
        for (DBPrefixIndex index : droppedPrefixIndexes) {
            String[] fields = Arrays.stream(index.getFieldIds()).mapToObj(table::getField).map(DBField::getName).toArray(String[]::new);
            createIndex(new TPrefixIndex(fields), tableName, namespace);
        }
        for (DBIntervalIndex index : droppedIntervalIndexes) {
            String indexField = table.getField(index.getIndexedFieldId()).getName();
            String[] hashFields = Arrays.stream(index.getHashFieldIds()).mapToObj(table::getField).map(DBField::getName).toArray(String[]::new);
            createIndex(new TIntervalIndex(indexField, hashFields), tableName, namespace);
        }
        for (DBRangeIndex index : droppedRangeIndexes) {
            String beginField = table.getField(index.getBeginFieldId()).getName();
            String endField = table.getField(index.getEndFieldId()).getName();
            String[] hashFields = Arrays.stream(index.getHashFieldIds()).mapToObj(table::getField).map(DBField::getName).toArray(String[]::new);
            createIndex(new TRangeIndex(beginField, endField, hashFields), tableName, namespace);
        }
        field.setName(newName);
        saveSchema();
    }

    @Deprecated
    private void createIndex(HashIndex index, DBTable dbTable, StructEntity table) throws DatabaseException {
        DBHashIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getHashIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doIndex(index, table, dbProvider);
            saveSchema();
        } else if (index.sortedFields.size() != 1 || !dbTable.getField(index.sortedFields.get(0).getName()).isForeignKey()) {
            throw new IndexAlreadyExistsException(index);
        }
    }

    public void createIndex(THashIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        createIndex(index, table);
    }

    public void createIndex(THashIndex index, DBTable dbTable) throws DatabaseException {
        DBHashIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getHashIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doIndex(dbIndex, dbTable, dbProvider);
            saveSchema();
        } else if (dbIndex.getFieldIds().length != 1 || !dbTable.getField(index.getFields()[0]).isForeignKey()) {
            throw new IndexAlreadyExistsException(dbIndex);
        }
    }

    @Deprecated
    private void createIndex(PrefixIndex index, DBTable dbTable, StructEntity table) throws DatabaseException {
        DBPrefixIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getPrefixIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doPrefixIndex(index, table, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(index);
        }
    }

    public void createIndex(TPrefixIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        createIndex(index, table);
    }

    public void createIndex(TPrefixIndex index, DBTable dbTable) throws DatabaseException {
        DBPrefixIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getPrefixIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doPrefixIndex(dbIndex, dbTable, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(dbIndex);
        }
    }

    @Deprecated
    private void createIndex(IntervalIndex index, DBTable dbTable, StructEntity table) throws DatabaseException {
        DBIntervalIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getIntervalIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doIntervalIndex(index, table, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(index);
        }
    }

    public void createIndex(TIntervalIndex index, DBTable dbTable) throws DatabaseException {
        DBIntervalIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getIntervalIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doIntervalIndex(dbIndex, dbTable, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(dbIndex);
        }
    }

    public void createIndex(TIntervalIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        createIndex(index, table);
    }

    @Deprecated
    private void createIndex(RangeIndex index, DBTable dbTable, StructEntity table) throws DatabaseException {
        DBRangeIndex dbIndex = DBTableUtils.buildIndex(index, dbTable);
        if (dbTable.getIntervalIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            dbTable.attachIndex(dbIndex);
            IndexService.doRangeIndex(index, table, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(index);
        }
    }

    public void createIndex(TRangeIndex index, DBTable table) throws DatabaseException {
        DBRangeIndex dbIndex = DBTableUtils.buildIndex(index, table);
        if (table.getRangeIndexes().stream().noneMatch(dbIndex::fieldsEquals)) {
            table.attachIndex(dbIndex);
            IndexService.doRangeIndex(dbIndex, table, dbProvider);
            saveSchema();
        } else {
            throw new IndexAlreadyExistsException(dbIndex);
        }
    }

    public void createIndex(TRangeIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        createIndex(index, table);
    }

    @Deprecated
    public boolean dropIndex(HashIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        if (index.sortedFields.size() == 1 && table.getField(index.sortedFields.get(0).getName()).isForeignKey()) {
            return true;
        }
        DBHashIndex targetIndex = DBTableUtils.buildIndex(index, table);
        dropIndex(table.getHashIndexes(), targetIndex::fieldsEquals, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return true;
    }

    public boolean dropIndex(THashIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        if (index.getFields().length == 1 && table.getField(index.getFields()[0]).isForeignKey()) {
            return true;
        }
        DBHashIndex targetIndex = DBTableUtils.buildIndex(index, table);
        dropIndex(table.getHashIndexes(), targetIndex::fieldsEquals, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return true;
    }

    public boolean dropIndex(TPrefixIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBPrefixIndex targetIndex = DBTableUtils.buildIndex(index, table);
        dropIndex(table.getPrefixIndexes(), targetIndex::fieldsEquals, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return true;
    }

    public boolean dropIndex(TIntervalIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBIntervalIndex targetIndex = DBTableUtils.buildIndex(index, table);
        dropIndex(table.getIntervalIndexes(), targetIndex::fieldsEquals, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return true;
    }

    public boolean dropIndex(TRangeIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBRangeIndex targetIndex = DBTableUtils.buildIndex(index, table);
        dropIndex(table.getRangeIndexes(), targetIndex::fieldsEquals, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return true;
    }

    private <T extends DBIndex> boolean dropIndex(List<T> indexes, Predicate<T> predicate, DBTable table) throws DatabaseException {
        for (T dbIndex : indexes) {
            if (predicate.test(dbIndex)) {
                dropIndexData(dbIndex, table);
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean dropIndex(PrefixIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBPrefixIndex targetIndex = DBTableUtils.buildIndex(index, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return dropIndex(table.getPrefixIndexes(), targetIndex::fieldsEquals, table);
    }

    @Deprecated
    public boolean dropIndex(IntervalIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBIntervalIndex targetIndex = DBTableUtils.buildIndex(index, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return dropIndex(table.getIntervalIndexes(), targetIndex::fieldsEquals, table);
    }

    @Deprecated
    public boolean dropIndex(RangeIndex index, String tableName, String namespace) throws DatabaseException {
        DBTable table = dbSchema.getTable(tableName, namespace);
        DBRangeIndex targetIndex = DBTableUtils.buildIndex(index, table);
        table.dropIndex(targetIndex);
        saveSchema();
        return dropIndex(table.getRangeIndexes(), targetIndex::fieldsEquals, table);
    }

    private void saveSchema() throws DatabaseException {
        saveSchema(dbSchema, dbProvider);
    }

    private void dropFieldData(DBField field, DBTable table) throws DatabaseException {
        try (DBTransaction transaction = dbProvider.beginTransaction()) {
            KeyPattern pattern = new KeyPattern(new KeyPattern.Postfix[]{
                    new KeyPattern.Postfix(FieldKey.ID_BYTE_SIZE, TypeConvert.pack(field.getName()))
            });
            try (DBIterator i = transaction.createIterator(table.getDataColumnFamily())) {
                for (KeyValue keyValue = i.seek(pattern); keyValue != null; keyValue = i.next()) {
                    transaction.singleDelete(table.getDataColumnFamily(), keyValue.getKey());
                }
            }
            transaction.commit();
        }
    }

    private void dropIndexData(DBIndex index, DBTable table) throws DatabaseException {
        try (DBTransaction transaction = dbProvider.beginTransaction()) {
            transaction.singleDeleteRange(table.getIndexColumnFamily(), new KeyPattern(index.getAttendant()));
            transaction.commit();
        }
    }

    private boolean hasDependenceOfOtherTable(int tableId) {
        for (DBTable table : getDbSchema().getTables()) {
            if (table.getId() == tableId) {
                continue;
            }
            for (DBField field : table.getSortedFields()) {
                if (field.isForeignKey() && field.getForeignTableId() == tableId) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<DBTable> getDependenciesOfOtherTable(int tableId) {
        List<DBTable> result = new ArrayList<>();
        for (DBTable table : getDbSchema().getTables()) {
            if (table.getId() == tableId) {
                continue;
            }
            for (DBField field : table.getSortedFields()) {
                if (field.isForeignKey() && field.getForeignTableId() == tableId) {
                    result.add(table);
                }
            }
        }
        return result;
    }
}
