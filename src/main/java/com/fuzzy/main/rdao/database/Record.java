package com.fuzzy.main.rdao.database;

import java.util.Arrays;
import java.util.Objects;

public class Record {

    private long id;
    private Object[] values;

    public Record(long id, Object[] values) {
        this.id = id;
        this.values = values;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        if (id != record.id) {
            return false;
        }
        if (values==record.values)
            return true;
        if (values==null || record.values==null)
            return false;

        int length = values.length;
        if (record.values.length != length)
            return false;

        for (int i=0; i<length; i++) {
            Object o1 = values[i];
            Object o2 = record.values[i];
            if (!(o1==null ? o2==null : o1 instanceof byte[] ? Arrays.equals((byte[]) o1, (byte[]) o2) : o1.equals(o2)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
