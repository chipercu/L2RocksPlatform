package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.fuzzy.main.rdao.database.domainobject.filter.IdFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.entityprovider.entity.AccessRoleEntity;
import com.fuzzy.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

import java.util.ArrayList;
import java.util.List;

public class AccessRoleDataSource implements DataSourceProvider<AccessRoleEntity> {


    private ReadableResource<AccessRoleReadable> accessRoleReadableResource;


    @Override
    public void prepare(ResourceProvider resources) {
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
    }

    @Override
    public DataSourceIterator<AccessRoleEntity> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException {

        List<AccessRoleEntity> accessRoles = new ArrayList<>();
        try (IteratorEntity<AccessRoleReadable> iterator = accessRoleReadableResource.findAll(new IdFilter(lastProcessedId + 1, Long.MAX_VALUE), transaction)) {
            while (iterator.hasNext() && accessRoles.size() < limit) {
                AccessRoleReadable accessRoleReadable = iterator.next();
                accessRoles.add(new AccessRoleEntity(accessRoleReadable));
            }
        }
        return new BaseSourceIterator<>(accessRoles);
    }
}
