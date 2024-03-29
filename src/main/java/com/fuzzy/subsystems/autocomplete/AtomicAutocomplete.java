package com.fuzzy.subsystems.autocomplete;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.util.HashSet;
import java.util.List;

public interface AtomicAutocomplete<T extends DomainObject> {

    List<AtomicAutocompleteItem<T>> get(
            final List<String> filterWords,
            final HashSet<Long> excluded,
            final ContextTransaction<?> context
    ) throws PlatformException;

}
