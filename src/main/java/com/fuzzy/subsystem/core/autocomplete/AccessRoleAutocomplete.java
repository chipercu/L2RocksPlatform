package com.fuzzy.subsystem.core.autocomplete;

import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.textfilter.AccessRoleTextFilterGetter;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;

public class AccessRoleAutocomplete extends LightAutocomplete<AccessRoleReadable> {

    public AccessRoleAutocomplete(ResourceProvider resources) {
        super(new AtomicAutocompleteImpl<>(
                new AccessRoleTextFilterGetter(resources),
                AccessRoleTextFilterGetter.FIELD_NAMES,
                null
        ));
    }
}
