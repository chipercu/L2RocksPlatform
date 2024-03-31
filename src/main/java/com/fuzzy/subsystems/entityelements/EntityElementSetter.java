package com.fuzzy.subsystems.entityelements;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityElementSetter<
        Node extends DomainObject,
        Item extends DomainObject,
        EntityNode extends DomainObject & EntityElementEditable<Long>,
        EntityItem extends DomainObject & EntityElementEditable<Long>,
        EntityAll extends DomainObject & EntityEditable> {

    private final ReadableResource<Node> nodeReadableResource;
    private final ReadableResource<Item> itemReadableResource;
    private final RemovableResource<EntityNode> entityNodeEditableResource;
    private final RemovableResource<EntityItem> entityItemEditableResource;
    private final RemovableResource<EntityAll> entityAllEditableResource;
    private final EntityElementRecalculator entityElementsRecalculator;
    private final int entityFieldNumber;
    private final int nodeFieldNumber;
    private final int itemFieldNumber;

    private class ElementValidator {
        private final Set<Long> validInsertedItems;
        private final Set<Long> validInsertedNodes;

        public ElementValidator(Collection<Long> nodes, Collection<Long> items, QueryTransaction transaction) throws PlatformException {
            PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(true);
            validInsertedNodes = nodeReadableResource != null && nodes != null ?
                    primaryKeyValidator.validate(nodes, nodeReadableResource, transaction) :
                    new HashSet<>();
            validInsertedItems = itemReadableResource != null && items != null ?
                    primaryKeyValidator.validate(items, itemReadableResource, transaction) :
                    new HashSet<>();
        }

        public Set<Long> getValidInsertedItems() {
            return validInsertedItems;
        }

        public Set<Long> getValidInsertedNodes() {
            return validInsertedNodes;
        }
    }

    public EntityElementSetter(
            ReadableResource<Node> nodeReadableResource,
            ReadableResource<Item> itemReadableResource,
            RemovableResource<EntityNode> entityNodeEditableResource,
            RemovableResource<EntityItem> entityItemEditableResource,
            RemovableResource<EntityAll> entityAllEditableResource,
            EntityElementRecalculator entityElementsRecalculator,
            int entityFieldNumber,
            int nodeFieldNumber,
            int itemFieldNumber) {
        this.nodeReadableResource = nodeReadableResource;
        this.itemReadableResource = itemReadableResource;
        this.entityNodeEditableResource = entityNodeEditableResource;
        this.entityItemEditableResource = entityItemEditableResource;
        this.entityAllEditableResource = entityAllEditableResource;
        this.entityElementsRecalculator = entityElementsRecalculator;
        this.entityFieldNumber = entityFieldNumber;
        this.nodeFieldNumber = nodeFieldNumber;
        this.itemFieldNumber = itemFieldNumber;
    }

    public void setAll(final Long entityId, QueryTransaction transaction) throws PlatformException {
        if (entityAllEditableResource != null) {
            EntityAll entityAll =
                    entityAllEditableResource.find(new HashFilter(entityFieldNumber, entityId), transaction);
            if (entityAll == null) {
                entityAll = entityAllEditableResource.create(transaction);
                entityAll.setEntityId(entityId);
                entityAllEditableResource.save(entityAll, transaction);
            }
        }
        removeAll(entityNodeEditableResource, transaction, entityId);
        removeAll(entityItemEditableResource, transaction, entityId);
    }

    public void set(
            final Long entityId,
            final Collection<Long> insertedNodes,
            final Collection<Long> insertedItems,
            final Collection<Long> removedNodes,
            final Collection<Long> removedItems,
            QueryTransaction transaction
    ) throws PlatformException {
        clearEntityAll(entityId, transaction);

        ElementValidator validator = new ElementValidator(insertedNodes, insertedItems, transaction);
        Set<Long> validInsertedNodes = validator.getValidInsertedNodes();
        Set<Long> validInsertedItems = validator.getValidInsertedItems();

        Set<Long> validRemovedNodes = new HashSet<>();
        if (removedNodes != null) {
            validRemovedNodes.addAll(removedNodes);
            validRemovedNodes.removeAll(validInsertedNodes);
        }

        Set<Long> validRemovedItems = new HashSet<>();
        if (removedItems != null) {
            validRemovedItems.addAll(removedItems);
            validRemovedItems.removeAll(validInsertedItems);
        }

        remove(
                entityNodeEditableResource,
                transaction,
                entityId,
                validRemovedNodes,
                nodeFieldNumber
        );
        remove(
                entityItemEditableResource,
                transaction,
                entityId,
                validRemovedItems,
                itemFieldNumber
        );
        insert(
                entityNodeEditableResource,
                transaction,
                entityId,
                validInsertedNodes,
                nodeFieldNumber
        );
        insert(
                entityItemEditableResource,
                transaction,
                entityId,
                validInsertedItems,
                itemFieldNumber
        );
        if (Objects.nonNull(entityElementsRecalculator)) {
            entityElementsRecalculator.recalculate(entityId, transaction);
        }
    }

    public void setState(
            Long entityId,
            Collection<Long> nodes,
            Collection<Long> items,
            QueryTransaction transaction
    ) throws PlatformException {
        clearEntityAll(entityId, transaction);
        removeAll(entityNodeEditableResource, transaction, entityId);
        removeAll(entityItemEditableResource, transaction, entityId);
        ElementValidator validator = new ElementValidator(nodes, items, transaction);
        insert(
                entityNodeEditableResource,
                transaction,
                entityId,
                validator.getValidInsertedNodes(),
                nodeFieldNumber
        );
        insert(
                entityItemEditableResource,
                transaction,
                entityId,
                validator.getValidInsertedItems(),
                itemFieldNumber
        );
        if (Objects.nonNull(entityElementsRecalculator)) {
            entityElementsRecalculator.recalculate(entityId, transaction);
        }
    }

    private void clearEntityAll(Long entityId, QueryTransaction transaction) throws PlatformException {
        EntityAll entityAll = entityAllEditableResource == null ? null :
                entityAllEditableResource.find(new HashFilter(entityFieldNumber, entityId), transaction);
        if (entityAll != null) {
            entityAllEditableResource.remove(entityAll, transaction);
        }
    }

    private <T extends DomainObject & DomainObjectEditable> void remove(
            RemovableResource<T> editableResource,
            QueryTransaction transaction,
            Long entityId,
            Collection<Long> removedElements,
            int elementFieldNumber
    ) throws PlatformException {
        if (editableResource != null) {
            HashFilter filter = new HashFilter(entityFieldNumber, entityId);
            for (Long elementId : removedElements) {
                filter.appendField(elementFieldNumber, elementId);
                editableResource.removeAll(filter, transaction);
            }
        }
    }

    private <T extends DomainObject & DomainObjectEditable> void removeAll(
            RemovableResource<T> editableResource,
            QueryTransaction transaction,
            Long entityId
    ) throws PlatformException {
        if (editableResource != null) {
            HashFilter filter = new HashFilter(entityFieldNumber, entityId);
            editableResource.removeAll(filter, transaction);
        }
    }

    private <T extends DomainObject & DomainObjectEditable & EntityElementEditable<Long>> void insert(
            EditableResource<T> editableResource,
            QueryTransaction transaction,
            Long entityId,
            Set<Long> insertedElements,
            int elementFieldNumber
    ) throws PlatformException {
        if (editableResource != null) {
            HashFilter filter = new HashFilter(entityFieldNumber, entityId);
            for (Long element : insertedElements) {
                filter.appendField(elementFieldNumber, element);
                if (!editableResource.findAll(filter, transaction).hasNext()) {
                    T access = editableResource.create(transaction);
                    access.setEntityId(entityId);
                    access.setElement(element);
                    editableResource.save(access, transaction);
                }
            }
        }
    }
}
