package com.fuzzy.main.entityprovidersdk.entity;

import com.fuzzy.main.entityprovidersdk.entity.datasource.DataSourceProvider;

public abstract class BaseDataSource<T extends DataContainer> implements DataSourceProvider<T> {

    private Class<T> domainClass;

    public BaseDataSource(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    public Class<T> getDomainClass() {
        return domainClass;
    }
}

