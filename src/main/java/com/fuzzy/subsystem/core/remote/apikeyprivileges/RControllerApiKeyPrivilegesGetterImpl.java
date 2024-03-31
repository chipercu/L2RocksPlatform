package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeReadable;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.fuzzy.subsystem.core.access.CorePrivilege.*;

public class RControllerApiKeyPrivilegesGetterImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerApiKeyPrivilegesGetter {

    private ReadableResource<ApiKeyCorePrivilegeReadable> apiKeyCorePrivilegeReadableResource;

    public RControllerApiKeyPrivilegesGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        apiKeyCorePrivilegeReadableResource = resources.getReadableResource(ApiKeyCorePrivilegeReadable.class);
    }

    @Override
    public HashMap<String, AccessOperationCollection> getPrivileges(long apiKeyId, ContextTransaction context) throws PlatformException {
        HashFilter filter = new HashFilter(ApiKeyCorePrivilegeReadable.FIELD_API_KEY_ID, apiKeyId);
        return apiKeyCorePrivilegeReadableResource.getAll(filter, context.getTransaction())
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getPrivilege().getUniqueKey(),
                        ApiKeyCorePrivilegeReadable::getOperations,
                        (v1, v2) -> {
                            v1.addOperation(v2.getOperations());
                            return v1;
                        },
                        HashMap::new
                ));
    }

    @Override
    public HashSet<String> getPrivilegeCollection() {
        return Arrays.stream(CorePrivilege.values())
                .filter(Predicate.isEqual(GRAPHQL_TOOL).negate())
                .filter(Predicate.isEqual(ACCESS_ROLE).negate())
                .filter(Predicate.isEqual(PRIVATE_SETTINGS).negate())
                .map(CorePrivilege::getUniqueKey)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
