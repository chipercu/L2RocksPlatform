package com.fuzzy.main.platform.component.frontend.context.source.impl;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.source.SourceGRequestAuth;

public class SourceGRequestAuthImpl extends SourceGRequestImpl implements SourceGRequestAuth {

    private UnauthorizedContext authContext;

    public SourceGRequestAuthImpl(GRequest request) {
        super(request);
        authContext = new UnauthorizedContext();
    }

    @Override
    public UnauthorizedContext getAuthContext() {
        return authContext;
    }

    public void setAuthContext(UnauthorizedContext authContext) {
        if (authContext == null) throw new IllegalArgumentException();
        this.authContext = authContext;
    }
}
