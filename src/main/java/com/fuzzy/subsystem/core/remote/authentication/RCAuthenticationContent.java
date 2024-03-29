package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationContent;

public interface RCAuthenticationContent extends QueryRemoteController {

    GAuthenticationContent getContent(long authenticationId, String type, ContextTransaction context)
            throws PlatformException;
}
