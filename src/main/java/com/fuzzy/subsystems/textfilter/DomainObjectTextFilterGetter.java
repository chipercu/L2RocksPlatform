package com.fuzzy.subsystems.textfilter;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.PrefixFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class DomainObjectTextFilterGetter<T extends DomainObject> implements TextFilterGetter<T> {

    private final ReadableResource<T> readableResource;
    private final Set<Integer> fieldNumbers;

    public DomainObjectTextFilterGetter(ReadableResource<T> readableResource, Set<Integer> fieldNumbers) {
        this.readableResource = readableResource;
        this.fieldNumbers = fieldNumbers;
    }

    @Override
    public IteratorEntity<T> findAll(String text, QueryTransaction transaction) throws PlatformException {
        if (StringUtils.isEmpty(text)) {
            return readableResource.iterator(transaction);
        }
        return readableResource.findAll(new PrefixFilter(fieldNumbers, text), transaction);
    }
}
