package com.fuzzy.database.schema;

import com.fuzzy.database.exception.IllegalTypeException;
import com.fuzzy.database.exception.StructEntityException;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.schema.TypeConverter;
import com.fuzzy.database.utils.TypeConvert;

import java.io.Serializable;

public class Field {

    private final int number;
    private final String name;
    private final byte[] nameBytes;
    private final Class<? extends Serializable> type;
    private final TypeConverter converter;
    private final StructEntity foreignDependency;

    Field(com.fuzzy.database.anotation.Field field, StructEntity parent) {
        this.number = field.number();
        this.name = field.name();
        this.nameBytes = TypeConvert.pack(field.name());
        this.type = field.type();
        this.converter = buildPacker(field.packerType());
        if (field.foreignDependency() != Class.class) {
            if (parent.getObjectClass() != field.foreignDependency()) {
                this.foreignDependency = Schema.resolve(field.foreignDependency());
            } else {
                this.foreignDependency = parent;
            }
        } else {
            this.foreignDependency = null;
        }

        if (isForeign() && this.type != Long.class) {
            throw new StructEntityException("Type of foreign field " + field.name() + " must be " + Long.class + ".");
        }
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public byte[] getNameBytes() {
        return nameBytes;
    }

    public Class<? extends Serializable> getType() {
        return type;
    }

    public TypeConverter getConverter() {
        return converter;
    }

    public boolean isForeign() {
        return foreignDependency != null;
    }

    public StructEntity getForeignDependency() {
        return foreignDependency;
    }

    public void throwIfNotMatch(Class type) {
        if (this.type != type) {
            throw new IllegalTypeException(this.type, type);
        }
    }

    private static TypeConverter buildPacker(Class<?> packerClass) {
        if (packerClass == Class.class) {
            return null;
        }

        try {
            return (TypeConverter) packerClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalTypeException(e);
        }
    }
}
