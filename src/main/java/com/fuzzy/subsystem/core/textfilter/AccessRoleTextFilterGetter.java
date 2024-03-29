package com.fuzzy.subsystem.core.textfilter;

import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Collections;
import java.util.Set;

public class AccessRoleTextFilterGetter extends DomainObjectTextFilterGetter<AccessRoleReadable> {

    public static final Set<Integer> FIELD_NAMES = Collections.singleton(AccessRoleReadable.FIELD_NAME);

    public AccessRoleTextFilterGetter(ResourceProvider resources) {
        super(
                resources.getReadableResource(AccessRoleReadable.class),
                FIELD_NAMES
        );
    }
}