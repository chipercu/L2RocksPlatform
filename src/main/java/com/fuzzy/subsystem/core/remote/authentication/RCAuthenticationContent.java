package com.fuzzy.subsystem.core.remote.authentication;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationContent;

public interface RCAuthenticationContent extends QueryRemoteController {

    GAuthenticationContent getContent(long authenticationId, String type, ContextTransaction context)
            throws PlatformException;
}
