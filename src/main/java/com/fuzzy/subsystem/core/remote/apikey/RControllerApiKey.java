package com.fuzzy.subsystem.core.remote.apikey;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;

import java.util.HashSet;

public interface RControllerApiKey extends QueryRemoteController {

    ApiKeyReadable create(ApiKeyBuilder builder, ContextTransaction context) throws PlatformException;

    ApiKeyReadable update(long apiKeyId, ApiKeyBuilder builder, ContextTransaction context) throws PlatformException;

    HashSet<Long> remove(HashSet<Long> apiKeyIds, ContextTransaction context) throws PlatformException;
}
