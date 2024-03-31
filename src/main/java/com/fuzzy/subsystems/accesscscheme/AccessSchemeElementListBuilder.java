package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.textfilter.TextFilterGetter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public abstract class AccessSchemeElementListBuilder<
        K extends Comparable<K>,
        T extends Identifiable<K> & AccessSchemeItem<?, ?, ? extends GAccessSchemeOperation>> extends BaseAccessSchemeElementListBuilder<K, Long, T> {

    private final TextFilterGetter<? extends DomainObject> textFilterGetter;

    public AccessSchemeElementListBuilder(@NonNull TextFilterGetter<? extends DomainObject> textFilterGetter) {
        this.textFilterGetter = textFilterGetter;
    }

    @Override
    protected void forEach(@NonNull String text,
                           @NonNull Consumer<T> handler,
                           @NonNull ContextTransaction<?> context) throws PlatformException {
        Set<Long> elements = new HashSet<>();
        textFilterGetter.forEach(text, element ->
                elements.add(element.getId()), context.getTransaction());
        forEach(item -> {
            if (elements.contains(getElementId(item))) {
                handler.accept(item);
            }
        }, context);
    }
}