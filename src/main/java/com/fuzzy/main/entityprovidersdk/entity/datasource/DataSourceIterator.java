package com.fuzzy.main.entityprovidersdk.entity.datasource;

import com.fuzzy.main.entityprovidersdk.entity.DataContainer;

public interface DataSourceIterator<T extends DataContainer> {

    boolean hasNext();

    T next();
}
