package com.fuzzy.main.rdao.database.schema;

import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.main.rdao.database.anotation.IntervalIndex;
import com.fuzzy.main.rdao.database.anotation.PrefixIndex;
import com.fuzzy.main.rdao.database.anotation.*;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.exception.FieldNotFoundException;
import com.fuzzy.main.rdao.database.exception.IndexNotFoundException;
import com.fuzzy.main.rdao.database.exception.StructEntityException;
import com.fuzzy.main.rdao.database.utils.ByteUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.util.*;
import java.util.stream.Collectors;

public class StructEntity {

    public static class Reference {

        public final Class<? extends DomainObject> objClass;
        public final com.fuzzy.main.rdao.database.schema.HashIndex fieldIndex;

        private Reference(Class<? extends DomainObject> objClass, com.fuzzy.main.rdao.database.schema.HashIndex fieldIndex) {
            this.objClass = objClass;
            this.fieldIndex = fieldIndex;
        }
    }

    public final static String NAMESPACE_SEPARATOR = ".";

    private final Class<? extends DomainObject> clazz;
    private final String name;
    private final String columnFamily;
    private final String indexColumnFamily;
    private final String namespace;
    private final com.fuzzy.main.rdao.database.schema.Field[] fields;
    private final Map<ByteArray, com.fuzzy.main.rdao.database.schema.Field> nameBytesFields;
    private final List<com.fuzzy.main.rdao.database.schema.HashIndex> hashIndexes;
    private final List<com.fuzzy.main.rdao.database.schema.PrefixIndex> prefixIndexes;
    private final List<com.fuzzy.main.rdao.database.schema.IntervalIndex> intervalIndexes;
    private final List<RangeIndex> rangeIndexes;
    private final List<Reference> referencingForeignFields = new ArrayList<>();

    public StructEntity(Class<? extends DomainObject> clazz) {
        final Entity annotationEntity = getAnnotationClass(clazz).getAnnotation(Entity.class);

        this.clazz = clazz;
        this.name = annotationEntity.name();
        this.columnFamily = buildColumnFamily(annotationEntity);
        this.indexColumnFamily = buildIndexColumnFamily(this.columnFamily);
        this.namespace = annotationEntity.namespace();

        Map<String, com.fuzzy.main.rdao.database.schema.Field> modifiableNameToFields = new HashMap<>(annotationEntity.fields().length);

        this.fields = new com.fuzzy.main.rdao.database.schema.Field[annotationEntity.fields().length];
        for(Field field: annotationEntity.fields()) {
            //Проверяем на уникальность
            if (modifiableNameToFields.containsKey(field.name())) {
                throw new StructEntityException("Field name=" + field.name() + " already exists into " + clazz.getName() + ".");
            }

            if (fields[field.number()] != null) {
                throw new StructEntityException("Field number=" + field.number() + " already exists into " + clazz.getName() + ".");
            }

            com.fuzzy.main.rdao.database.schema.Field f = new com.fuzzy.main.rdao.database.schema.Field(field, this);
            fields[field.number()] = f;

            modifiableNameToFields.put(f.getName(), f);

            if (f.isForeign()) {
                registerToForeignEntity(f);
            }
        }

        this.nameBytesFields = new HashMap<>(fields.length);
        Arrays.stream(fields).forEach(field -> nameBytesFields.put(new ByteArray(field.getNameBytes()), field));
        this.hashIndexes = buildHashIndexes(annotationEntity);
        this.prefixIndexes = buildPrefixIndexes(annotationEntity);
        this.intervalIndexes = buildIntervalIndexes(annotationEntity);
        this.rangeIndexes = buildRangeIndexes(annotationEntity);
    }

    private void registerToForeignEntity(com.fuzzy.main.rdao.database.schema.Field foreignField) {
        foreignField.getForeignDependency().referencingForeignFields.add(new Reference(clazz, buildForeignIndex(foreignField)));
    }

    public String getName() {
        return name;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getIndexColumnFamily() {
        return indexColumnFamily;
    }

    public Class<? extends DomainObject> getObjectClass() {
        return clazz;
    }

    public com.fuzzy.main.rdao.database.schema.Field getField(ByteArray name) {
        com.fuzzy.main.rdao.database.schema.Field field = nameBytesFields.get(name);
        if (field == null) {
            throw new FieldNotFoundException(clazz, name.convertToString());
        }
        return field;
    }

    public com.fuzzy.main.rdao.database.schema.Field getField(int number) {
        if (number >= fields.length) {
            throw new FieldNotFoundException(clazz, "number=" + number);
        }
        return fields[number];
    }

    public com.fuzzy.main.rdao.database.schema.Field[] getFields() {
        return fields;
    }

    public Set<String> getFieldNames(Collection<Integer> fieldNumbers) {
        if (fieldNumbers == null) {
            return null;
        }

        return fieldNumbers.stream()
                .map(i -> getField(i).getName())
                .collect(Collectors.toSet());
    }

    public com.fuzzy.main.rdao.database.schema.HashIndex getHashIndex(Collection<Integer> indexedFields) {
        for (com.fuzzy.main.rdao.database.schema.HashIndex index : hashIndexes) {
            if (index.sortedFields.size() != indexedFields.size()) {
                continue;
            }

            if (index.sortedFields.stream().allMatch(f -> indexedFields.contains(f.getNumber()))) {
                return index;
            }
        }

        throw new IndexNotFoundException(com.fuzzy.main.rdao.database.schema.HashIndex.toString(getFieldNames(indexedFields)), clazz);
    }

    public com.fuzzy.main.rdao.database.schema.PrefixIndex getPrefixIndex(Collection<Integer> indexedFields) {
        for (com.fuzzy.main.rdao.database.schema.PrefixIndex index : prefixIndexes) {
            if (index.sortedFields.size() != indexedFields.size()) {
                continue;
            }

            if (index.sortedFields.stream().allMatch(f -> indexedFields.contains(f.getNumber()))) {
                return index;
            }
        }

        throw new IndexNotFoundException(com.fuzzy.main.rdao.database.schema.PrefixIndex.toString(getFieldNames(indexedFields)), clazz);
    }

    public com.fuzzy.main.rdao.database.schema.IntervalIndex getIntervalIndex(Collection<Integer> hashedFields, Integer indexedField) {
        for (com.fuzzy.main.rdao.database.schema.IntervalIndex index : intervalIndexes) {
            if (index.sortedFields.size() != (hashedFields.size() + 1)) {
                continue;
            }

            if (index.getIndexedField().getNumber() != indexedField) {
                continue;
            }

            if (index.getHashedFields().stream().allMatch(f -> hashedFields.contains(f.getNumber()))) {
                return index;
            }
        }

        throw new IndexNotFoundException(com.fuzzy.main.rdao.database.schema.IntervalIndex.toString(getFieldNames(hashedFields), getField(indexedField).getName()), clazz);
    }

    public RangeIndex getRangeIndex(Collection<Integer> hashedFields, int beginField, int endField) {
        for (RangeIndex index : rangeIndexes) {
            if (index.sortedFields.size() != (hashedFields.size() + 2)) {
                continue;
            }

            if (index.getBeginIndexedField().getNumber() != beginField || index.getEndIndexedField().getNumber() != endField) {
                continue;
            }

            if (index.getHashedFields().stream().allMatch(f -> hashedFields.contains(f.getNumber()))) {
                return index;
            }
        }

        throw new IndexNotFoundException(RangeIndex.toString(getFieldNames(hashedFields), getField(beginField).getName(), getField(endField).getName()), clazz);
    }

    public List<com.fuzzy.main.rdao.database.schema.HashIndex> getHashIndexes() {
        return hashIndexes;
    }

    public List<com.fuzzy.main.rdao.database.schema.PrefixIndex> getPrefixIndexes() {
        return prefixIndexes;
    }

    public List<com.fuzzy.main.rdao.database.schema.IntervalIndex> getIntervalIndexes() {
        return intervalIndexes;
    }

    public List<RangeIndex> getRangeIndexes() {
        return rangeIndexes;
    }

    public List<Reference> getReferencingForeignFields() {
        return referencingForeignFields;
    }

    public static Class<? extends DomainObject> getAnnotationClass(Class<? extends DomainObject> clazz) {
        while (!clazz.isAnnotationPresent(Entity.class)) {
            if (!DomainObject.class.isAssignableFrom(clazz.getSuperclass())) {
                throw new StructEntityException("Not found " + Entity.class + " annotation in " + clazz + ".");
            }
            clazz = (Class<? extends DomainObject>) clazz.getSuperclass();
        }
        return clazz;
    }

    private static String buildColumnFamily(Entity entity) {
        return entity.namespace() + NAMESPACE_SEPARATOR + entity.name();
    }

    private static String buildIndexColumnFamily(String parentColumnFamily) {
        return parentColumnFamily + NAMESPACE_SEPARATOR + "index";
    }

    private List<com.fuzzy.main.rdao.database.schema.HashIndex> buildHashIndexes(Entity entity) {
        List<com.fuzzy.main.rdao.database.schema.HashIndex> result = new ArrayList<>(entity.hashIndexes().length);
        Set<Integer> singleFieldHashes = new HashSet<>();
        for (com.fuzzy.main.rdao.database.schema.Field field : fields) {
            if (!field.isForeign()) {
                continue;
            }
            result.add(buildForeignIndex(field));
            singleFieldHashes.add(field.getNumber());
        }
        for (HashIndex index: entity.hashIndexes()) {
            if (index.fields().length != 1 || !singleFieldHashes.contains(index.fields()[0])) {
                result.add(new com.fuzzy.main.rdao.database.schema.HashIndex(index, this));
            }
        }
        checkExclusiveAttendants(result);
        return Collections.unmodifiableList(result);
    }

    private com.fuzzy.main.rdao.database.schema.HashIndex buildForeignIndex(com.fuzzy.main.rdao.database.schema.Field foreignField) {
        return new com.fuzzy.main.rdao.database.schema.HashIndex(foreignField, this);
    }

    private List<com.fuzzy.main.rdao.database.schema.PrefixIndex> buildPrefixIndexes(Entity entity) {
        if (entity.prefixIndexes().length == 0) {
            return Collections.emptyList();
        }

        List<com.fuzzy.main.rdao.database.schema.PrefixIndex> result = new ArrayList<>(entity.prefixIndexes().length);
        for (PrefixIndex index: entity.prefixIndexes()) {
            result.add(new com.fuzzy.main.rdao.database.schema.PrefixIndex(index, this));
        }
        checkExclusiveAttendants(result);
        return Collections.unmodifiableList(result);
    }

    private List<com.fuzzy.main.rdao.database.schema.IntervalIndex> buildIntervalIndexes(Entity entity) {
        if (entity.intervalIndexes().length == 0) {
            return Collections.emptyList();
        }

        List<com.fuzzy.main.rdao.database.schema.IntervalIndex> result = new ArrayList<>(entity.intervalIndexes().length);
        for (IntervalIndex index: entity.intervalIndexes()) {
            result.add(new com.fuzzy.main.rdao.database.schema.IntervalIndex(index, this));
        }
        checkExclusiveAttendants(result);
        return Collections.unmodifiableList(result);
    }

    private List<RangeIndex> buildRangeIndexes(Entity entity) {
        if (entity.rangeIndexes().length == 0) {
            return Collections.emptyList();
        }

        List<RangeIndex> result = new ArrayList<>(entity.rangeIndexes().length);
        for (com.fuzzy.main.rdao.database.anotation.RangeIndex index: entity.rangeIndexes()) {
            result.add(new RangeIndex(index, this));
        }
        checkExclusiveAttendants(result);
        return Collections.unmodifiableList(result);
    }

    private static <T extends BaseIndex> void checkExclusiveAttendants(List<T> indexes) {
        for (int i = 0; i < indexes.size(); i++) {
            T index = indexes.get(i);
            for (int j = i + 1; j < indexes.size(); j++) {
                if (Arrays.equals(index.attendant, indexes.get(j).attendant)) {
                    throw new StructEntityException("Attendant of " + index.getClass().getSimpleName() + " not unique");
                }
            }
        }
    }

    public static class ByteArray {

        private final byte[] array;
        private final int from, to;
        private final int hashCode;

        public ByteArray(byte[] array) {
            this(array, 0, array.length);
        }

        public ByteArray(byte[] array, int from, int to) {
            this.array = array;
            this.from = from;
            this.to = to;
            this.hashCode = hashCode(array, from, to);
        }

        private String convertToString() {
            return TypeConvert.unpackString(array, from, to - from);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ByteArray)) return false;

            ByteArray byteArray = (ByteArray) o;

            return ByteUtils.equals(array, from, to, byteArray.array, byteArray.from, byteArray.to);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        private static int hashCode(byte[] array, int from, int to) {
            int result = 1;
            for (int i = from; i < to; ++i) {
                result = 31 * result + array[i];
            }
            return result;
        }
    }
}

