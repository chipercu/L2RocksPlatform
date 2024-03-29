package com.fuzzy.subsystems.autocomplete;

import com.infomaximum.database.domainobject.DomainObject;

public class AtomicAutocompleteItem<T extends DomainObject> {
    private T object;
    private long weight;

    public AtomicAutocompleteItem(T object, long weight) {
        this.object = object;
        this.weight = weight;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
