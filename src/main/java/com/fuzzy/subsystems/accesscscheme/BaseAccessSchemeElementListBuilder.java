package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.list.AbstractListBuilder;
import com.fuzzy.subsystems.remote.Identifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public abstract class BaseAccessSchemeElementListBuilder<
        K extends Comparable<K>,
        E,
        T extends Identifiable<K> & AccessSchemeItem<?, ?, ? extends GAccessSchemeOperation>> extends AbstractListBuilder<K, T> {

    public BaseAccessSchemeElementListBuilder() {
    }

    protected abstract @NonNull E getElementId(@NonNull T item);

    protected abstract boolean checkElementAccess(@NonNull E elementId,
                                                  @NonNull ContextTransaction<?> context) throws PlatformException;

    @Override
    protected void forEach(@NonNull Set<K> ids,
                           @NonNull Consumer<T> handler,
                           @NonNull ContextTransaction<?> context) throws PlatformException {
        forEach(item -> {
            if (ids.contains(item.getIdentifier())) {
                handler.accept(item);
            }
        }, context);
    }

    @Override
    protected void forEachAlwaysComingItems(@NonNull Set<K> ids,
                                            @NonNull Consumer<T> handler,
                                            @NonNull ContextTransaction<?> context) throws PlatformException {
        forEach(ids, handler, context);
    }

    @Override
    protected boolean checkItem(@NonNull T item, @NonNull ContextTransaction<?> context) throws PlatformException {
        return checkElementAccess(getElementId(item), context);
    }
}
