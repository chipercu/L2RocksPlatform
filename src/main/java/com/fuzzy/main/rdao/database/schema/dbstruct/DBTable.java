package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.rdao.database.domainobject.filter.IntervalFilter;
import com.fuzzy.main.rdao.database.domainobject.filter.PrefixFilter;
import com.fuzzy.main.rdao.database.domainobject.filter.RangeFilter;
import com.fuzzy.main.rdao.database.exception.FieldNotFoundException;
import com.fuzzy.main.rdao.database.exception.IndexNotFoundException;
import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DBTable extends DBObject {

    private static final String JSON_PROP_NAME = "name";
    private static final String JSON_PROP_NAMESPACE = "namespace";
    private static final String JSON_PROP_FIELDS = "fields";
    private static final String JSON_PROP_HASH_INDEXES = "hash_indexes";
    private static final String JSON_PROP_PREFIX_INDEXES = "prefix_indexes";
    private static final String JSON_PROP_INTERVAL_INDEXES = "interval_indexes";
    private static final String JSON_PROP_RANGE_INDEXES = "range_indexes";

    private final String dataColumnFamily;
    private final String indexColumnFamily;

    private String name;
    private final String namespace;
    private final List<DBField> sortedFields;
    private final List<DBHashIndex> hashIndexes;
    private final List<DBPrefixIndex> prefixIndexes;
    private final List<DBIntervalIndex> intervalIndexes;
    private final List<DBRangeIndex> rangeIndexes;

    private final Map<String, DBField> fieldNameFieldMap;

    private DBTable(int id, String name, String namespace, List<DBField> sortedFields,
                    List<DBHashIndex> hashIndexes, List<DBPrefixIndex> prefixIndexes,
                    List<DBIntervalIndex> intervalIndexes, List<DBRangeIndex> rangeIndexes) {
        super(id);
        this.dataColumnFamily = namespace + StructEntity.NAMESPACE_SEPARATOR + name;
        this.indexColumnFamily = namespace + StructEntity.NAMESPACE_SEPARATOR + name + ".index";
        this.name = name;
        this.namespace = namespace;
        this.sortedFields = sortedFields;
        this.hashIndexes = hashIndexes;
        this.prefixIndexes = prefixIndexes;
        this.intervalIndexes = intervalIndexes;
        this.rangeIndexes = rangeIndexes;
        this.fieldNameFieldMap = sortedFields.stream().collect(Collectors.toMap(DBField::getName, dbField -> dbField));
    }

    DBTable(int id, String name, String namespace, List<DBField> sortedFields) {
        this(id, name, namespace, sortedFields, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public String getDataColumnFamily() {
        return dataColumnFamily;
    }

    public String getIndexColumnFamily() {
        return indexColumnFamily;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void dropField(int id) {
        fieldNameFieldMap.remove(sortedFields.get(id).getName());
        for (int i = id + 1; i < sortedFields.size(); i++) {
            DBField field = sortedFields.get(i);
            field.setId(field.getId() - 1);
        }
        sortedFields.remove(id);
        decrementIndexFieldIdsAfterId(id);
    }

    public void dropIndex(DBHashIndex index) {
        dropIndex(index, hashIndexes);
    }

    public void dropIndex(DBPrefixIndex index) {
        dropIndex(index, prefixIndexes);
    }

    public void dropIndex(DBIntervalIndex index) {
        dropIndex(index, intervalIndexes);
    }

    public void dropIndex(DBRangeIndex index) {
        dropIndex(index, rangeIndexes);
    }

    public List<DBField> getSortedFields() {
        return Collections.unmodifiableList(sortedFields);
    }

    public DBField newField(String name, Class<? extends Serializable> type, Integer foreignTableId) {
        DBField field = new DBField(DBSchema.nextId(sortedFields), name, type, foreignTableId);
        sortedFields.add(field);
        fieldNameFieldMap.put(field.getName(), field);
        return field;
    }

    public DBField insertNewField(int fieldId, String name, Class<? extends Serializable> type, Integer foreignTableId) {
        DBField newField = new DBField(fieldId, name, type, foreignTableId);
        fieldNameFieldMap.remove(sortedFields.get(fieldId).getName());
        fieldNameFieldMap.put(name, newField);
        sortedFields.add(fieldId, newField);

        for (int i = fieldId + 1; i < sortedFields.size(); i++) {
            DBField field = sortedFields.get(i);
            field.setId(field.getId() + 1);
        }
        incrementIndexFieldIdsAfterId(fieldId);
        return newField;
    }

    public int findFieldIndex(String fieldName) {
        DBField field = fieldNameFieldMap.get(fieldName);
        return field != null ? field.getId() : -1;
    }

    public boolean containField(String fieldName) throws SchemaException {
        return findFieldIndex(fieldName) != -1;
    }

    public int getFieldIndex(String fieldName) throws SchemaException {
        int i = findFieldIndex(fieldName);
        if (i == -1) {
            throw new FieldNotFoundException(fieldName, getName());
        }
        return i;
    }

    public DBField getField(String fieldName) throws SchemaException {
        return sortedFields.get(getFieldIndex(fieldName));
    }

    public DBField getField(int id) throws SchemaException {
        if (id >= sortedFields.size()) {
            throw new FieldNotFoundException(id, getName());
        }
        return sortedFields.get(id);
    }

    public DBField[] getFields(int[] ids) throws SchemaException {
        DBField[] fields = new DBField[ids.length];
        for (int i = 0; i < ids.length; i++) {
            fields[i] = sortedFields.get(ids[i]);
        }
        return fields;
    }

    public List<DBHashIndex> getHashIndexes() {
        return hashIndexes;
    }

    public List<DBPrefixIndex> getPrefixIndexes() {
        return prefixIndexes;
    }

    public List<DBIntervalIndex> getIntervalIndexes() {
        return intervalIndexes;
    }

    public List<DBRangeIndex> getRangeIndexes() {
        return rangeIndexes;
    }

    public Stream<? extends DBIndex> getIndexesStream() {
        return Stream.concat(Stream.concat(Stream.concat(
                hashIndexes.stream(),
                prefixIndexes.stream()),
                intervalIndexes.stream()),
                rangeIndexes.stream());
    }

    public void attachIndex(DBHashIndex index) {
        attachIndex(index, hashIndexes);
    }

    public void attachIndex(DBPrefixIndex index) {
        attachIndex(index, prefixIndexes);
    }

    public void attachIndex(DBIntervalIndex index) {
        attachIndex(index, intervalIndexes);
    }

    public void attachIndex(DBRangeIndex index) {
        attachIndex(index, rangeIndexes);
    }

    public DBHashIndex getIndex(HashFilter filter) {
        Set<Integer> indexedFieldIds = filter.getValues().keySet();
        return hashIndexes.stream()
                .filter(index -> index.getFieldIds().length == indexedFieldIds.size()
                        && Arrays.stream(index.getFieldIds()).allMatch(indexedFieldIds::contains))
                .findAny()
                .orElseThrow(() -> new IndexNotFoundException(indexedFieldIds, this));
    }

    public DBPrefixIndex getIndex(PrefixFilter filter) {
        Set<Integer> indexedFieldIds = filter.getFieldNames();
        return prefixIndexes.stream()
                .filter(index -> index.getFieldIds().length == indexedFieldIds.size()
                        && Arrays.stream(index.getFieldIds()).allMatch(indexedFieldIds::contains))
                .findAny()
                .orElseThrow(() -> new IndexNotFoundException(indexedFieldIds, this));
    }

    public DBIntervalIndex getIndex(IntervalFilter filter) {
        Set<Integer> indexedFieldIds = filter.getHashedValues().keySet();
        return intervalIndexes.stream()
                .filter(index -> index.getFieldIds().length == indexedFieldIds.size() + 1
                        && index.getIndexedFieldId() == filter.getIndexedFieldId()
                        && Arrays.stream(index.getHashFieldIds()).allMatch(indexedFieldIds::contains))
                .findAny()
                .orElseThrow(() -> {
                    indexedFieldIds.add(filter.getIndexedFieldId());
                    return new IndexNotFoundException(indexedFieldIds, this);
                });
    }

    public DBRangeIndex getIndex(RangeFilter filter) {RangeFilter.IndexedField indexedField = filter.getIndexedField();
        Set<Integer> indexedFieldIds = filter.getHashedValues().keySet();
        return rangeIndexes.stream()
                .filter(index -> index.getFieldIds().length == indexedFieldIds.size() + 2
                        && index.getBeginFieldId() == indexedField.beginField && index.getEndFieldId() == indexedField.endField
                        && Arrays.stream(index.getHashFieldIds()).allMatch(indexedFieldIds::contains))
                .findAny()
                .orElseThrow(() -> {
                    indexedFieldIds.add(indexedField.beginField);
                    indexedFieldIds.add(indexedField.endField);
                    return new IndexNotFoundException(indexedFieldIds, this);
                });
    }

    void checkIntegrity() throws SchemaException {
        DBSchema.checkUniqueId(sortedFields);
        DBSchema.checkUniqueId(getIndexesStream().collect(Collectors.toList()));
        checkFieldsOrder();

        Set<Integer> indexingFieldIds = new HashSet<>(sortedFields.size());

        for (DBHashIndex i : hashIndexes) {
            IntStream.of(i.getFieldIds()).forEach(indexingFieldIds::add);
        }

        for (DBPrefixIndex i : prefixIndexes) {
            IntStream.of(i.getFieldIds()).forEach(indexingFieldIds::add);
        }

        for (DBIntervalIndex i : intervalIndexes) {
            indexingFieldIds.add(i.getIndexedFieldId());
            IntStream.of(i.getHashFieldIds()).forEach(indexingFieldIds::add);
        }

        for (DBRangeIndex i : rangeIndexes) {
            indexingFieldIds.add(i.getBeginFieldId());
            indexingFieldIds.add(i.getEndFieldId());
            IntStream.of(i.getHashFieldIds()).forEach(indexingFieldIds::add);
        }

        Set<Integer> existingFieldIds = sortedFields.stream().map(DBObject::getId).collect(Collectors.toSet());
        indexingFieldIds.stream()
                .filter(fieldId -> !existingFieldIds.contains(fieldId))
                .findFirst()
                .ifPresent(fieldId -> {
                    throw new SchemaException("Field id=" + fieldId + " not found into '" + getName() + "'");
                });
    }

    private <T extends DBIndex> void attachIndex(T index, List<T> destination) {
        index.setId(DBSchema.nextId(getIndexesStream()));
        destination.add(index);
    }

    private void checkFieldsOrder() {
        int id = 0;
        for (DBField sortedField : sortedFields) {
            if (sortedField.getId() != id) {
                throw new SchemaException("Table " + namespace + "." + name + " has inconsistent fields order: " + sortedFields.stream()
                        .map(DBObject::getId)
                        .collect(Collectors.toList()));
            }
            id++;
        }
    }

    private <T extends DBIndex> void dropIndex(T index, List<T> indexes) {
        for (int i = 0; i < indexes.size(); i++) {
            if (index.fieldsEquals(indexes.get(i))) {
                indexes.remove(i);
                return;
            }
        }
    }

    static DBTable fromJson(JSONObject source) throws SchemaException {
        List<DBField> fields = JsonUtils.toList(JSON_PROP_FIELDS, source, DBField::fromJson);
        return new DBTable(
                JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                JsonUtils.getValue(JSON_PROP_NAME, String.class, source),
                JsonUtils.getValue(JSON_PROP_NAMESPACE, String.class, source),
                fields,
                JsonUtils.toList(JSON_PROP_HASH_INDEXES, source, s -> DBHashIndex.fromJson(s, fields)),
                JsonUtils.toList(JSON_PROP_PREFIX_INDEXES, source, s ->  DBPrefixIndex.fromJson(s, fields)),
                JsonUtils.toList(JSON_PROP_INTERVAL_INDEXES, source, s ->  DBIntervalIndex.fromJson(s, fields)),
                JsonUtils.toList(JSON_PROP_RANGE_INDEXES, source, s ->  DBRangeIndex.fromJson(s, fields))
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_NAME, name);
        object.put(JSON_PROP_NAMESPACE, namespace);
        object.put(JSON_PROP_FIELDS, JsonUtils.toJsonArray(sortedFields));
        object.put(JSON_PROP_HASH_INDEXES, JsonUtils.toJsonArray(hashIndexes));
        object.put(JSON_PROP_PREFIX_INDEXES, JsonUtils.toJsonArray(prefixIndexes));
        object.put(JSON_PROP_INTERVAL_INDEXES, JsonUtils.toJsonArray(intervalIndexes));
        object.put(JSON_PROP_RANGE_INDEXES, JsonUtils.toJsonArray(rangeIndexes));
        return object;
    }

    private void decrementIndexFieldIdsAfterId(int id) {
        for (int i = 0; i < hashIndexes.size(); i++) {
            DBHashIndex index = hashIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId > id)) {
                DBField[] fields = Arrays.stream(index.getFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId > id ? fieldId - 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBHashIndex newHashIndex = new DBHashIndex(index.getId(), fields);
                hashIndexes.set(i, newHashIndex);
            }
        }

        for (int i = 0; i < prefixIndexes.size(); i++) {
            DBPrefixIndex index = prefixIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId > id)) {
                DBField[] fields = Arrays.stream(index.getFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId > id ? fieldId - 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBPrefixIndex newIndex = new DBPrefixIndex(index.getId(), fields);
                prefixIndexes.set(i, newIndex);
            }
        }

        for (int i = 0; i < intervalIndexes.size(); i++) {
            DBIntervalIndex index = intervalIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId > id)) {
                int realIndexedFieldId = index.getIndexedFieldId() > id ? index.getIndexedFieldId() - 1 : index.getIndexedFieldId();
                DBField indexedField = sortedFields.get(realIndexedFieldId);
                DBField[] hashedFieldIds = Arrays.stream(index.getHashFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId > id ? fieldId - 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBIntervalIndex newIndex = new DBIntervalIndex(index.getId(), indexedField, hashedFieldIds);
                intervalIndexes.set(i, newIndex);
            }
        }

        for (int i = 0; i < rangeIndexes.size(); i++) {
            DBRangeIndex index = rangeIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId > id)) {
                int realBeginFieldId = index.getBeginFieldId() > id ? index.getBeginFieldId() - 1 : index.getBeginFieldId();
                int realEndFieldId = index.getEndFieldId() > id ? index.getEndFieldId() - 1 : index.getEndFieldId();
                DBField beginField = sortedFields.get(realBeginFieldId);
                DBField endField = sortedFields.get(realEndFieldId);
                DBField[] hashedFieldIds = Arrays.stream(index.getHashFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId > id ? fieldId - 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBRangeIndex newIndex = new DBRangeIndex(index.getId(), beginField, endField, hashedFieldIds);
                rangeIndexes.set(i, newIndex);
            }
        }
    }

    private void incrementIndexFieldIdsAfterId(int id) {
        for (int i = 0; i < hashIndexes.size(); i++) {
            DBHashIndex index = hashIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId >= id)) {
                DBField[] fields = Arrays.stream(index.getFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId >= id ? fieldId + 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBHashIndex newHashIndex = new DBHashIndex(index.getId(), fields);
                hashIndexes.set(i, newHashIndex);
            }
        }

        for (int i = 0; i < prefixIndexes.size(); i++) {
            DBPrefixIndex index = prefixIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId >= id)) {
                DBField[] fields = Arrays.stream(index.getFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId >= id ? fieldId + 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBPrefixIndex newIndex = new DBPrefixIndex(index.getId(), fields);
                prefixIndexes.set(i, newIndex);
            }
        }

        for (int i = 0; i < intervalIndexes.size(); i++) {
            DBIntervalIndex index = intervalIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId >= id)) {
                int realIndexedFieldId = index.getIndexedFieldId() > id ? index.getIndexedFieldId() + 1 : index.getIndexedFieldId();
                DBField indexedField = sortedFields.get(realIndexedFieldId);
                DBField[] hashedFieldIds = Arrays.stream(index.getHashFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId >= id ? fieldId + 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBIntervalIndex newIndex = new DBIntervalIndex(index.getId(), indexedField, hashedFieldIds);
                intervalIndexes.set(i, newIndex);
            }
        }

        for (int i = 0; i < rangeIndexes.size(); i++) {
            DBRangeIndex index = rangeIndexes.get(i);
            if (Arrays.stream(index.getFieldIds()).anyMatch(fieldId -> fieldId >= id)) {
                int realBeginFieldId = index.getBeginFieldId() > id ? index.getBeginFieldId() + 1 : index.getBeginFieldId();
                int realEndFieldId = index.getEndFieldId() > id ? index.getEndFieldId() + 1 : index.getEndFieldId();
                DBField beginField = sortedFields.get(realBeginFieldId);
                DBField endField = sortedFields.get(realEndFieldId);
                DBField[] hashedFieldIds = Arrays.stream(index.getHashFieldIds()).mapToObj(fieldId -> {
                    int realFieldId = fieldId >= id ? fieldId + 1 : fieldId;
                    return sortedFields.get(realFieldId);
                }).toArray(DBField[]::new);
                DBRangeIndex newIndex = new DBRangeIndex(index.getId(), beginField, endField, hashedFieldIds);
                rangeIndexes.set(i, newIndex);
            }
        }
    }
}
