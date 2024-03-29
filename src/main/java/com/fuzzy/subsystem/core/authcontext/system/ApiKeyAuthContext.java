package com.fuzzy.subsystem.core.authcontext.system;

import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ApiKeyAuthContext extends SystemAuthContext {

    private final long apiKeyId;
    private final String apiKeyName;

    public ApiKeyAuthContext(
            @NonNull HashMap<String, AccessOperationCollection> privileges,
            long apiKeyId,
            @NonNull String apiKeyName
    ) {
        super(privileges);
        this.apiKeyId = apiKeyId;
        this.apiKeyName = apiKeyName;

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(apiKeyId));
        params.put("name", apiKeyName);
        addParams(params);
    }

    public long getApiKeyId() {
        return apiKeyId;
    }

    @Override
    public String getUniqueId() {
        return "ApiKey" + apiKeyId;
    }

    @Override
    public String toString() {
        return "ApiKeyAuthContext(" + apiKeyId + ')';
    }

    public String getApiKeyName() {
        return apiKeyName;
    }
}
