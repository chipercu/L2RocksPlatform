package com.fuzzy.main.entityprovidersdk.entity.datasource;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.entityprovidersdk.entity.BaseDataSource;
import com.fuzzy.main.entityprovidersdk.entity.BaseSourceIterator;
import com.fuzzy.main.entityprovidersdk.entity.DataContainer;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.filter.IdFilter;

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
