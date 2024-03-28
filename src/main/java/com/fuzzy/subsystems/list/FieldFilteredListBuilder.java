package com.fuzzy.subsystems.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.textfilter.TextFilterEnumerator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Set;

public class FieldFilteredListBuilder<T extends DomainObject> {

    private final InnerListBuilder<T> builder;

    public FieldFilteredListBuilder(ReadableResource<T> readableResource,
                                    TextFilterEnumerator<T> textFilterEnumerator,
                                    ListChecker<T> checker,
                                    int filterFieldNumber) {
        builder = new InnerListBuilder<>(readableResource, textFilterEnumerator, checker, filterFieldNumber);
    }

    public void setComparator(SorterComparator<T> itemComparator) {
        builder.setComparator(itemComparator);
    }

    public ListResult<T> build(ListParam param, long filterValue, ContextTransaction<?> context) throws PlatformException {
        builder.setFilterValue(filterValue);
        return builder.build(param, context);
    }

    private static class InnerListBuilder<T extends DomainObject> extends ListBuilder<T> {

        private final ReadableResource<T> readableResource;
        private final int filterFieldNumber;
        private long filterValue;

        public InnerListBuilder(ReadableResource<T> readableResource,
                                TextFilterEnumerator<T> textFilterEnumerator,
                                ListChecker<T> checker,
                                int filterFieldNumber) {
            super(readableResource, textFilterEnumerator, checker);
            this.readableResource = readableResource;
            this.filterFieldNumber = filterFieldNumber;
        }

        public void setFilterValue(long filterValue) {
            this.filterValue = filterValue;
        }

        @Override
        protected void forEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
            HashFilter filter = new HashFilter(filterFieldNumber, filterValue);
            readableResource.forEach(filter, handler::accept, context.getTransaction());
        }

        @Override
        protected void forEach(@NonNull Set<Long> ids, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
            super.forEach(ids, object -> {
                if (checkObject(object)) {
                    handler.accept(object);
                }
            }, context);
        }

        @Override
        protected void forEach(@NonNull String text, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
            super.forEach(text, object -> {
                if (checkObject(object)) {
                    handler.accept(object);
                }
            }, context);
        }

        @Override
        protected void forEachAlwaysComingItems(@NonNull Set<Long> ids, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
            super.forEachAlwaysComingItems(ids, object -> {
                if (checkObject(object)) {
                    handler.accept(object);
                }
            }, context);
        }

        @Override
        protected boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException {
            return super.checkItem(item, context) && checkObject(item);
        }

        private boolean checkObject(T object) {
            return Objects.equals(object.get(filterFieldNumber), filterValue);
        }
    }
}
