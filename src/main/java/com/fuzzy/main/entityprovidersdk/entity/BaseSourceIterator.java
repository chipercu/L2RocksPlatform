package com.fuzzy.main.entityprovidersdk.entity;

import com.fuzzy.main.entityprovidersdk.entity.datasource.DataSourceIterator;

import java.util.Iterator;
import java.util.List;

public class BaseSourceIterator<T extends DataContainer> implements DataSourceIterator<T> {


    private final Iterator<T> iterator;

    public BaseSourceIterator(List<T> domainObjectList) {
        iterator = domainObjectList.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
