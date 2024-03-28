package com.fuzzy.subsystems.readableresourcecache;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;

import java.util.HashMap;
import java.util.Map;

public class ReadableResourceCache<T extends DomainObject> {

    private ReadableResource<T> readableResource;
    private Map<Long, T> cache;

    public ReadableResourceCache(ResourceProvider resources, Class<T> resClass) {
        readableResource = resources.getReadableResource(resClass);
        cache = new HashMap<>();
    }

    public void put(T object) {
        cache.put(object.getId(), object);
    }

    public T get(long id, QueryTransaction transaction) throws PlatformException {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        T object = readableResource.get(id, transaction);
        cache.put(id, object);
        return object;
    }
}
