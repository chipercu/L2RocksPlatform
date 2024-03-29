package com.fuzzy.main.entityprovidersdk.entity.datasource;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.main.entityprovidersdk.entity.DataContainer;

import java.util.List;

public interface DataSourceProvider<T extends DataContainer> {

    void prepare(ResourceProvider resourceProvider);

    default void setExtractFields(List<EntityFieldInfo> fields) {

    }

    DataSourceIterator<T> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException;
}
