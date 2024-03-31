package com.fuzzy.subsystem.entityprovidersdk.entity.datasource;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;

import java.util.List;

public interface DataSourceProvider<T extends DataContainer> {

    void prepare(ResourceProvider resourceProvider);

    default void setExtractFields(List<EntityFieldInfo> fields) {

    }

    DataSourceIterator<T> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException;
}
