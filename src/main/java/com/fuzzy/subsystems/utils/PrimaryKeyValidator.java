package com.fuzzy.subsystems.utils;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.input.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PrimaryKeyValidator {

    private final boolean exceptionIgnorable;

    public PrimaryKeyValidator(boolean exceptionIgnorable) {
        this.exceptionIgnorable = exceptionIgnorable;
    }

    public <T extends DomainObject> T validateAndGet(
            Long inKey,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        T object = inKey != null ? readableResource.get(inKey, transaction) : null;
        if (object == null && !exceptionIgnorable) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(readableResource.getDomainClass(), inKey);
        }
        return object;
    }

    public <T extends DomainObject> boolean validate(
            Long inKey,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        return validateAndGet(inKey, readableResource, transaction) != null;
    }

    public <T extends DomainObject> HashSet<Long> validate(
            Collection<Long> inKeys,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        HashSet<Long> outKeys = null;
        if (inKeys != null) {
            outKeys = new HashSet<>();
            for (Long key : inKeys) {
                if (validate(key, readableResource, transaction)) {
                    outKeys.add(key);
                }
            }
        }
        return outKeys;
    }

    public <T extends DomainObject> Set<Long> validate(
            GInputItems inFilter,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        Set<Long> outFilter = null;
        if (inFilter != null && inFilter.isSpecified()) {
            outFilter = validate(inFilter.getItems(), readableResource, transaction);
        }
        return outFilter;
    }

    public <T extends DomainObject, Y extends DomainObject> GInputNodesItems validate(
            GInputNodesItems inFilter,
            ReadableResource<T> nodeReadableResource,
            ReadableResource<Y> itemReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        if (inFilter == null) {
            return null;
        }
        return new GInputNodesItems(
                validate(inFilter.getNodes(), nodeReadableResource, transaction),
                validate(inFilter.getNodes(), itemReadableResource, transaction)
        );
    }

    public <Y extends DomainObject> GItemsFilter validate(
            GItemsFilter inFilter,
            ReadableResource<Y> itemReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        if (inFilter == null) {
            return null;
        }
        HashSet<Long> items = null;
        switch (inFilter.getOperation()) {
            case INCLUDE, EXCLUDE -> {
                if (inFilter.getItems() == null) {
                    throw GeneralExceptionBuilder.buildInvalidValueException("items_filter");
                }
                items = validate(inFilter.getItems(), itemReadableResource, transaction);
            }
        }
        return new GItemsFilter(inFilter.getOperation(), items);
    }

    public <T extends DomainObject, Y extends DomainObject> GStandardFilter validate(
            GStandardFilter inFilter,
            ReadableResource<T> nodeReadableResource,
            ReadableResource<Y> itemReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        if (inFilter == null) {
            return null;
        }
        HashSet<Long> nodes = null;
        HashSet<Long> items = null;
        switch (inFilter.getOperation()) {
            case INCLUDE, EXCLUDE -> {
                if (inFilter.getNodes() == null && inFilter.getItems() == null) {
                    throw GeneralExceptionBuilder.buildInvalidValueException("standard_filter");
                }
                if (inFilter.getNodes() != null) {
                    nodes = validate(inFilter.getNodes(), nodeReadableResource, transaction);
                }
                if (inFilter.getItems() != null) {
                    items = validate(inFilter.getItems(), itemReadableResource, transaction);
                }
            }
        }
        return new GStandardFilter(inFilter.getOperation(), nodes, items);
    }

    public <T extends DomainObject> GTreePaging validate(
            GTreePaging inPaging,
            ReadableResource<T> nodeReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        GTreePaging outPaging = null;
        if (inPaging != null) {
            ArrayList<GPagingElement> outElements = null;
            if (inPaging.getElements() != null && nodeReadableResource != null) {
                outElements = new ArrayList<>();
                for (GPagingElement element : inPaging.getElements()) {
                    if (element != null && validate(element.getId(), nodeReadableResource, transaction)) {
                        outElements.add(element);
                    }
                }
            }
            outPaging = new GTreePaging(outElements, inPaging.getRootLimit(), inPaging.getDefaultLimit());
        }
        return outPaging;
    }

    public <T extends DomainObject> GPagingEx validate(
            GPagingEx inPaging,
            ReadableResource<T> elementReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        GPagingEx outPaging = null;
        if (inPaging != null) {
            ArrayList<GPagingElement> outElements = null;
            if (inPaging.getElements() != null && elementReadableResource != null) {
                outElements = new ArrayList<>();
                for (GPagingElement element : inPaging.getElements()) {
                    if (validate(element.getId(), elementReadableResource, transaction)) {
                        outElements.add(element);
                    }
                }
            }
            outPaging = new GPagingEx(outElements, inPaging.getDefaultLimit());
        }
        return outPaging;
    }
}
