package com.fuzzy.subsystem.entityprovidersdk.entity.datasource;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.IdFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.entityprovidersdk.entity.BaseDataSource;
import com.fuzzy.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;

import java.util.ArrayList;
import java.util.List;


public class DomainObjectDataSource<T extends DataContainer> extends BaseDataSource<T> {


    private ReadableResource<? extends DomainObject> readableResource;

    public DomainObjectDataSource(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    public void prepare(ResourceProvider resourceProvider) {
        readableResource = resourceProvider.getReadableResource(getDomainClass().asSubclass(DomainObject.class));
    }

    @Override
    public DataSourceIterator<T> createIterator(long start, int limit, QueryTransaction transaction) throws PlatformException {

        List<T> domainObjects = new ArrayList<>();
        try (IteratorEntity<? extends DomainObject> it = readableResource.findAll(new IdFilter(start + 1, Long.MAX_VALUE), transaction)) {
            int counter = 0;
            while (it.hasNext() && counter++ < limit) {
                domainObjects.add((T) it.next());
            }

        }
        return new BaseSourceIterator(domainObjects);
    }
}
