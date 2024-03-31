package com.fuzzy.platform.component.frontend.context.source;

import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.source.SourceGRequest;

public interface SourceGRequestAuth extends SourceGRequest {

    UnauthorizedContext getAuthContext();
}
