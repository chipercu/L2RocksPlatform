package com.fuzzy.subsystems.list;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.textfilter.TextFilterEnumerator;
import com.fuzzy.subsystems.utils.DomainObjectIdentifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListBuilder<T extends DomainObject> {

    private final ReadableResource<T> readableResource;
    private final TextFilterEnumerator<T> textFilterEnumerator;
    private final ListChecker<T> checker;
    private final InnerListBuilder<T> builder;

    public ListBuilder(ReadableResource<T> readableResource,
                       TextFilterEnumerator<T> textFilterEnumerator,
                       ListChecker<T> checker
    ) {
        super();
        this.readableResource = readableResource;
        this.textFilterEnumerator = textFilterEnumerator;
        this.checker = checker;
        this.builder = new InnerListBuilder<>(this);
    }

    public void setComparator(SorterComparator<T> itemComparator) {
        builder.setComparator((o1, o2) -> itemComparator.compare(o1.object(), o2.object()));
    }

    public ListResult<T> build(ListParam<Long> param, ContextTransaction<?> context) throws PlatformException {
        ListResult<DomainObjectIdentifiable<T>> result = builder. build(param, context);
        List<ListItem<T>> items = result.items().stream()
                .map(o -> new ListItem<>(o.item().object(), o.selected(), o.hidden()))
                .collect(Collectors.toList());
        return new ListResult<>(items, result.matchCount(), result.hasNext());
    }

    protected void forEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
        readableResource.forEach(handler::accept, context.getTransaction());
    }

    protected void forEach(@NonNull Set<Long> ids, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
        for (Long id : ids) {
            if (id != null) {
                T object = readableResource.get(id, context.getTransaction());
                if (object != null) {
                    handler.accept(object);
                }
            }
        }
    }

    protected void forEach(@NonNull String text, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
        if (textFilterEnumerator == null) {
            throw GeneralExceptionBuilder.buildUnexpectedBehaviourException("Не найден TextFilterGetter");
        }
        textFilterEnumerator.forEach(text, handler, context.getTransaction());
    }

    protected void forEachAlwaysComingItems(@NonNull Set<Long> ids, Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
        forEach(ids, handler, context);
    }

    protected boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException {
        return checker == null || checker.checkItem(item, context);
    }

    private static class InnerListBuilder<T extends DomainObject> extends AbstractListBuilder<Long, DomainObjectIdentifiable<T>> {

        private final ListBuilder<T> builder;

        public InnerListBuilder(ListBuilder<T> builder) {
            this.builder = builder;
        }

        @Override
        protected void forEach(@NonNull Consumer<DomainObjectIdentifiable<T>> handler,
                               @NonNull ContextTransaction<?> context) throws PlatformException {
            builder.forEach(new ConsumerWrapper<>(handler), context);
        }

        @Override
        protected void forEach(@NonNull Set<Long> ids,
                               @NonNull Consumer<DomainObjectIdentifiable<T>> handler,
                               @NonNull ContextTransaction<?> context) throws PlatformException {
            builder.forEach(ids, new ConsumerWrapper<>(handler), context);
        }

        @Override
        protected void forEach(@NonNull String text,
                               @NonNull Consumer<DomainObjectIdentifiable<T>> handler,
                               @NonNull ContextTransaction<?> context) throws PlatformException {
            builder.forEach(text, new ConsumerWrapper<>(handler), context);
        }

        @Override
        protected void forEachAlwaysComingItems(@NonNull Set<Long> ids,
                                                @NonNull Consumer<DomainObjectIdentifiable<T>> handler,
                                                @NonNull ContextTransaction<?> context) throws PlatformException {
            builder.forEachAlwaysComingItems(ids, new ConsumerWrapper<>(handler), context);
        }

        @Override
        protected boolean checkItem(@NonNull DomainObjectIdentifiable<T> item,
                                    @NonNull ContextTransaction<?> context) throws PlatformException {
            return builder.checkItem(item.object(), context);
        }

        private record ConsumerWrapper<T extends DomainObject>(
                Consumer<DomainObjectIdentifiable<T>> consumer) implements Consumer<T> {

            @Override
            public void accept(T t) throws PlatformException {
                consumer.accept(new DomainObjectIdentifiable<>(t));
            }
        }
    }
}
