package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.database.domainobject.DomainObject;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DomainObjectStandardAccessSchemeItem<S, O, U extends DomainObject & AccessSchemeItem<S, O, StandardAccessOperation>>
        extends StandardAccessSchemeItem<Long, S, O> {

    private final U item;

    public DomainObjectStandardAccessSchemeItem(@NonNull U item) {
        super(item);
        this.item = item;
    }

    @Override
    public @NonNull Long getIdentifier() {
        return item.getId();
    }
}
