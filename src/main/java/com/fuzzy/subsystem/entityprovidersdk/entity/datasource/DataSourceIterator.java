package com.fuzzy.subsystem.entityprovidersdk.entity.datasource;

import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;

public interface DataSourceIterator<T extends DataContainer> {

    boolean hasNext();

    T next();
}
