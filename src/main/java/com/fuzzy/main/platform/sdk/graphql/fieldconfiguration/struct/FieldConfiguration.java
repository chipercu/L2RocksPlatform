package com.fuzzy.main.platform.sdk.graphql.fieldconfiguration.struct;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;

public class FieldConfiguration implements RemoteObject {

    public final Class<? extends UnauthorizedContext>[] typeAuthContexts;

    public FieldConfiguration(Class<? extends UnauthorizedContext>... typeAuthContexts) {
        this.typeAuthContexts = typeAuthContexts;
    }

}
