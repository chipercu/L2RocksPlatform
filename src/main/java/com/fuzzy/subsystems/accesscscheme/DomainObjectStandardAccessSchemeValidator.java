package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DomainObjectStandardAccessSchemeValidator<
        K extends DomainObject,
        T extends DomainObject & AccessSchemeItem<Long, Long, StandardAccessOperation>,
        U extends DomainObject & AccessSchemeItem<Long, Long, StandardAccessOperation>>
        extends StandardAccessSchemeValidator<Long, K, T, U> {

    public DomainObjectStandardAccessSchemeValidator(@NonNull AccessSchemeItemGetter<Long, Long, StandardAccessOperation, T> employeeAccessGetter,
                                                     @Nullable AccessSchemeItemGetter<Long, Long, StandardAccessOperation, U> apiKeyAccessGetter) {
        super(employeeAccessGetter, apiKeyAccessGetter);
    }

    @Override
    protected @NonNull Long getId(@NonNull K object) throws PlatformException {
        return object.getId();
    }
}
