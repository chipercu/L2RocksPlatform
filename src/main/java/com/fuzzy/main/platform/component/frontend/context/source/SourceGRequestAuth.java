package com.fuzzy.main.platform.component.frontend.context.source;

import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;

public interface SourceGRequestAuth extends SourceGRequest {

    UnauthorizedContext getAuthContext();
}
