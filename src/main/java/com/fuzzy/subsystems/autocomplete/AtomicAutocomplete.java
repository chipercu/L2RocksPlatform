package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;

import java.util.HashSet;
import java.util.List;

public interface AtomicAutocomplete<T extends DomainObject> {

    List<AtomicAutocompleteItem<T>> get(
            final List<String> filterWords,
            final HashSet<Long> excluded,
            final ContextTransaction<?> context
    ) throws PlatformException;

}
