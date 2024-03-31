package com.fuzzy.subsystem.core.remote.apikey;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;

import java.util.HashSet;

public interface RControllerApiKey extends QueryRemoteController {

    ApiKeyReadable create(ApiKeyBuilder builder, ContextTransaction context) throws PlatformException;

    ApiKeyReadable update(long apiKeyId, ApiKeyBuilder builder, ContextTransaction context) throws PlatformException;

    HashSet<Long> remove(HashSet<Long> apiKeyIds, ContextTransaction context) throws PlatformException;
}
