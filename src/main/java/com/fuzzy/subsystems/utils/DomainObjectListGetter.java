package com.fuzzy.subsystems.utils;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.comparators.IdentifiableComparator;
import com.fuzzy.subsystems.sorter.Sorter;
import com.fuzzy.subsystems.sorter.SorterComparator;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DomainObjectListGetter<T extends DomainObject> {

    public class Result {
        public ArrayList<T> items = null;
        public boolean hasNext;
    }

    public interface Checker<T extends DomainObject> {
        boolean check(T object, QueryTransaction transaction) throws PlatformException;
    }

    private final ReadableResource<T> readableResource;

    public DomainObjectListGetter(ReadableResource <T> readableResource) {
        this.readableResource = readableResource;
    }

    public Result get(
            Filter filter,
            Integer limit,
            SorterComparator<DomainObjectIdentifiable<T>> comparator,
            Checker<T> checker,
            QueryTransaction transaction
    ) throws PlatformException {
        SorterComparator<DomainObjectIdentifiable<T>> innerComparator = comparator;
        if (innerComparator == null) {
            innerComparator = new IdentifiableComparator<>();
        }
        Sorter<DomainObjectIdentifiable<T>> sorter = new Sorter <>(innerComparator, limit);
        try (IteratorEntity<T> ie = filter == null ?
                readableResource.iterator(transaction) : readableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                T object = ie.next();
                if (checker == null || checker.check(object, transaction)) {
                    sorter.add(new DomainObjectIdentifiable<>(object));
                }
            }
        }
        Result result = new Result();
        result.items = sorter.getData().stream()
                .map(DomainObjectIdentifiable::object)
                .collect(Collectors.toCollection(ArrayList::new));
        result.hasNext = sorter.hasNext();
        return result;
    }

    public Result get(Filter filter, Integer limit, SorterComparator<DomainObjectIdentifiable<T>> comparator, QueryTransaction transaction)
            throws PlatformException {
        return get(filter, limit, comparator, null, transaction);
    }
}
