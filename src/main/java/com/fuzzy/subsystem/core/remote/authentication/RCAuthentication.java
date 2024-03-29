package com.fuzzy.subsystem.core.remote.authentication;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;

public interface RCAuthentication extends QueryRemoteController {

    AuthenticationReadable create(AuthenticationCreatingBuilder builder, ContextTransaction context) throws PlatformException;

    AuthenticationReadable update(long authenticationId, AuthenticationUpdatingBuilder builder, ContextTransaction context)
            throws PlatformException;

    boolean remove(long authenticationId, ContextTransaction context) throws PlatformException;
}
