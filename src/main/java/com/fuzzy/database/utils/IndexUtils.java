package com.fuzzy.database.utils;

import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.utils.TypeConvert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IndexUtils {

    public static List<Field> buildIndexedFields(int[] indexedFields, StructEntity parent) {
        return Arrays.stream(indexedFields)
                .mapToObj(parent::getField)
                .sorted(Comparator.comparing(f -> f.getName().toLowerCase())) //Сортируем, что бы хеш не ломался из-за перестановки местами полей
                .collect(Collectors.toList());
    }

    public static byte[] buildFieldsHashCRC32(List<Field> indexedFields) {
        StringBuilder stringBuilder = new StringBuilder();
        indexedFields.forEach(field -> stringBuilder.append(field.getName()).append(':').append(field.getType().getName()).append('.'));
        return TypeConvert.packCRC32(stringBuilder.toString());
    }

    public static byte[] buildFieldsHashCRC32(DBField[] indexedFields) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(indexedFields).forEach(field -> stringBuilder.append(field.getName()).append(':').append(field.getType().getName()).append('.'));
        return TypeConvert.packCRC32(stringBuilder.toString());
    }

    public static DBField[] getFieldsByIds(List<DBField> tableFields, int... destinationFields) {
        DBField[] result = new DBField[destinationFields.length];
        for (int i = 0; i < destinationFields.length; i++) {
            int destFieldId = destinationFields[i];
            result[i] = getFieldsByIds(tableFields, destFieldId);
        }
        return result;
    }

    public static DBField getFieldsByIds(List<DBField> tableFields, int destinationField) {
        return tableFields.stream()
                .filter(field -> destinationField == field.getId())
                .findAny()
                .orElseThrow(() -> new SchemaException("Destination field id=" + destinationField + " doesn't found in table"));
    }
}
