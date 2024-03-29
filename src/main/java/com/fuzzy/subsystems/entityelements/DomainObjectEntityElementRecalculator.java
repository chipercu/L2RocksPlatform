package com.fuzzy.subsystems.entityelements;

import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.EditableResource;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DomainObjectEntityElementRecalculator<
        EntityNode extends EntityElementReadable<Long> & EntityElementEditable<Long>,
        EntityItem extends EntityElementReadable<Long> & EntityElementEditable<Long>,
        EntityAll extends EntityReadable & EntityEditable>
        implements EntityElementRecalculator {

    private final RemovableResource<EntityNode> entityNodeResource;
    private final RemovableResource<EntityItem> entityItemResource;
    private final EditableResource<EntityAll> entityAllResource;
    private final NodeItemGrouping elementGrouping;
    private final int entityFieldNumber;
    private final int nodeFieldNumber;
    private final int itemFieldNumber;

    public DomainObjectEntityElementRecalculator(
            RemovableResource<EntityNode> entityNodeResource,
            RemovableResource<EntityItem> entityItemResource,
            EditableResource<EntityAll> entityAllResource,
            NodeItemGrouping elementGrouping,
            int entityFieldNumber,
            int nodeFieldNumber,
            int itemFieldNumber) {
        this.entityNodeResource = entityNodeResource;
        this.entityItemResource = entityItemResource;
        this.entityAllResource = entityAllResource;
        this.elementGrouping = elementGrouping;
        this.entityFieldNumber = entityFieldNumber;
        this.nodeFieldNumber = nodeFieldNumber;
        this.itemFieldNumber = itemFieldNumber;
    }

    @Override
    public void recalculate(QueryTransaction transaction) throws PlatformException {
        recalculate(null, transaction);
    }

    @Override
    public void recalculate(Long entityId, QueryTransaction transaction) throws PlatformException {
        if (entityAllResource != null) {
            removeChildrenForAllElements(entityId, transaction);
        }
        Map<Long, Set<Long>> entityNodes = getEntityElements(entityId, entityNodeResource, transaction);
        Map<Long, Set<Long>> entityItems = getEntityElements(entityId, entityItemResource, transaction);
        for (Map.Entry<Long, Set<Long>> pair : entityNodes.entrySet()) {
            removeChildren(pair.getKey(), pair.getValue(), entityItems.get(pair.getKey()), transaction);
        }
    }

    private void removeChildrenForAllElements(Long entityId, QueryTransaction transaction) throws PlatformException {
        HashFilter entityFilter = getEntityFilter(entityId);
        try (IteratorEntity<EntityAll> ie = entityFilter == null ?
                entityAllResource.iterator(transaction) :
                entityAllResource.findAll(entityFilter, transaction)
        ) {
            while (ie.hasNext()) {
                Filter filter = new HashFilter(entityFieldNumber, ie.next().getEntityId());
                entityNodeResource.removeAll(filter, transaction);
                entityItemResource.removeAll(filter, transaction);
            }
        }
    }

    private <T extends EntityElementReadable<Long>> Map<Long, Set<Long>> getEntityElements(
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        return getEntityElements(null, readableResource, transaction);
    }

    private <T extends EntityElementReadable<Long>> Map<Long, Set<Long>> getEntityElements(
            Long entityId,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        Map<Long, Set<Long>> entityElements = new HashMap<>();

        HashFilter filter = getEntityFilter(entityId);
        try (IteratorEntity<T> ie = filter == null ?
                readableResource.iterator(transaction) :
                readableResource.findAll(filter, transaction)
        ) {
            while (ie.hasNext()) {
                T entityElement = ie.next();
                if (filter == null) {
                    entityId = entityElement.getEntityId();
                }
                Set<Long> elements = entityElements.get(entityId);
                if (elements == null) {
                    elements = new HashSet<>();
                    entityElements.put(entityId, elements);
                }
                elements.add(entityElement.getElement());
            }
        }
        return entityElements;
    }

    private HashFilter getEntityFilter(Long entityId) {
        return entityId == null ? null : new HashFilter(entityFieldNumber, entityId);
    }

    private void removeChildren(Long entityId, Set<Long> nodes, Set<Long> items, QueryTransaction transaction)
            throws PlatformException {
        if (!nodes.isEmpty()) {
            Set<Long> childNodes = elementGrouping.getChildNodesRecursively(nodes, transaction);

            HashFilter filter = new HashFilter(entityFieldNumber, entityId);
            for (Long nodeId : nodes) {
                if (childNodes.contains(nodeId)) {
                    filter.appendField(nodeFieldNumber, nodeId);
                    entityNodeResource.removeAll(filter, transaction);
                }
            }
            if (items != null && !items.isEmpty()) {
                childNodes.addAll(nodes);
                Set<Long> childItems = elementGrouping.getChildItems(childNodes, transaction);
                filter = new HashFilter(entityFieldNumber, entityId);
                for (Long itemId : items) {
                    if (childItems.contains(itemId)) {
                        filter.appendField(itemFieldNumber, itemId);
                        entityItemResource.removeAll(filter, transaction);
                    }
                }
            }
        }
    }
}
