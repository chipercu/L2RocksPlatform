package com.fuzzy.subsystem.core.autocomplete;

import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.textfilter.AuthenticationTextFilterGetter;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;

public class AuthenticationAutocomplete extends LightAutocomplete<AuthenticationReadable> {

    public AuthenticationAutocomplete(ResourceProvider resources) {
        super(new AtomicAutocompleteImpl<>(
                new AuthenticationTextFilterGetter(resources),
                AuthenticationTextFilterGetter.FIELD_NAMES,
                null
        ));
    }
}