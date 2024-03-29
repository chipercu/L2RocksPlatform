package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.accessroleprivileges.AccessRolePrivilegesGetter;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.entityprovider.entity.PrivilegeAccessRoleEntity;
import com.infomaximum.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.subsystem.Translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivilegeAccessRoleDataSource implements DataSourceProvider<PrivilegeAccessRoleEntity> {

    private AccessRolePrivilegesGetter accessRolePrivilegesGetter;
    private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private Translator translator;

    public PrivilegeAccessRoleDataSource() {
    }

    @Override
    public void prepare(ResourceProvider resources) {
        translator = new Translator();
        accessRolePrivilegesGetter = new AccessRolePrivilegesGetter(resources);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
    }

    @Override
    public DataSourceIterator<PrivilegeAccessRoleEntity> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException {
        List<PrivilegeAccessRoleEntity> privileges = new ArrayList<>();
        if (lastProcessedId > 0) {
            return new BaseSourceIterator<>(privileges);
        }
        long num = 0;
        try (IteratorEntity<AccessRoleReadable> iterator = accessRoleReadableResource.iterator(transaction)) {
            while (iterator.hasNext()) {
                final AccessRoleReadable accessRoleReadable = iterator.next();
                long accessRoleId = accessRoleReadable.getId();
                final HashMap<String, AccessOperationCollection> accessRolePrivileges = accessRolePrivilegesGetter.getPrivileges(accessRoleId, new ContextTransactionImpl(new SourceSystemImpl(), transaction));
                for (Map.Entry<String, AccessOperationCollection> privilegeEntry : accessRolePrivileges.entrySet()) {
                    final PrivilegeAccessRoleEntity.Builder accessRolePrivilegeBuilder = PrivilegeAccessRoleEntity.newBuilder();
                    accessRolePrivilegeBuilder.withAccessRoleId(accessRoleId)
                            .withId(++num)
                            .withName(privilegeEntry.getKey())
                            .withNameRu(translator.translate(privilegeEntry.getKey(), Language.RUSSIAN));
                    final AccessOperationCollection accessOperationCollection = privilegeEntry.getValue();
                    if (!accessOperationCollection.isEmpty()) {
                        accessRolePrivilegeBuilder.withCreate(accessOperationCollection.contains(AccessOperation.CREATE))
                                .withWrite(accessOperationCollection.contains(AccessOperation.WRITE))
                                .withRead(accessOperationCollection.contains(AccessOperation.READ))
                                .withDelete(accessOperationCollection.contains(AccessOperation.DELETE))
                                .withExecute(accessOperationCollection.contains(AccessOperation.EXECUTE));
                    }
                    privileges.add(accessRolePrivilegeBuilder.build());
                }
            }
        }
        return new BaseSourceIterator<>(privileges);
    }
}