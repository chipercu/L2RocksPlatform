package com.fuzzy.subsystems.entityelements;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.function.Function;

public class DomainObjectEntityElementEnumerator<
        T,
        EntityNode extends EntityElementReadable<T>,
        EntityItem extends EntityElementReadable<T>,
        EntityAll extends EntityReadable>
        implements EntityElementEnumerator<T> {

    private final ReadableResource<EntityNode> entityNodeReadableResource;
    private final ReadableResource<EntityItem> entityItemReadableResource;
    private final ReadableResource<EntityAll> entityAllReadableResource;
    private final int entityFieldNumber;

    public DomainObjectEntityElementEnumerator(
            ReadableResource<EntityNode> entityNodeReadableResource,
            ReadableResource<EntityItem> entityItemReadableResource,
            ReadableResource<EntityAll> entityAllReadableResource,
            int entityFieldNumber) {
        this.entityNodeReadableResource = entityNodeReadableResource;
        this.entityItemReadableResource = entityItemReadableResource;
        this.entityAllReadableResource = entityAllReadableResource;
        this.entityFieldNumber = entityFieldNumber;
    }

    @Override
    public void forEachNode(final Long entityId, QueryTransaction transaction, Function<T, Boolean> function)
            throws PlatformException {
        forEach(entityNodeReadableResource, entityId, transaction, function);
    }

    @Override
    public void forEachItem(final Long entityId, QueryTransaction transaction, Function<T, Boolean> function)
            throws PlatformException {
        forEach(entityItemReadableResource, entityId, transaction, function);
    }

    @Override
    public boolean isAll(final Long entityId, QueryTransaction transaction) throws PlatformException {
        return entityAllReadableResource != null &&
                entityAllReadableResource.find(new HashFilter(entityFieldNumber, entityId), transaction) != null;
    }

    private <Y extends EntityElementReadable<T>> void forEach(
            ReadableResource<Y> readableResource,
            Long entityId,
            QueryTransaction transaction,
            Function<T, Boolean> function
    ) throws PlatformException {
        if (readableResource != null) {
            try (IteratorEntity<Y> ie = readableResource.findAll(new HashFilter(entityFieldNumber, entityId), transaction)) {
                while (ie.hasNext()) {
                    Y entityElement = ie.next();
                    if (!function.apply(entityElement.getElement())) {
                        break;
                    }
                }
            }
        }
    }
}
