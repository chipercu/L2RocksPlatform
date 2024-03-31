package com.fuzzy.subsystem.entityprovidersdk.entity;

import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

public abstract class BaseDataSource<T extends DataContainer> implements DataSourceProvider<T> {

    private Class<T> domainClass;

    public BaseDataSource(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    public Class<T> getDomainClass() {
        return domainClass;
    }
}

