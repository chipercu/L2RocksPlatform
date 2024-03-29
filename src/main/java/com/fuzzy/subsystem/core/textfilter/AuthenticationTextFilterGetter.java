package com.fuzzy.subsystem.core.textfilter;

import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Collections;
import java.util.Set;

public class AuthenticationTextFilterGetter extends DomainObjectTextFilterGetter<AuthenticationReadable> {

    public static final Set<Integer> FIELD_NAMES = Collections.singleton(AuthenticationReadable.FIELD_NAME);

    public AuthenticationTextFilterGetter(ResourceProvider resources) {
        super(
                resources.getReadableResource(AuthenticationReadable.class),
                FIELD_NAMES
        );
    }
}