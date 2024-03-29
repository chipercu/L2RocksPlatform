package com.fuzzy.subsystem.core.remote.tag;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.tag.TagEditable;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.resourceswithnotifications.RemovableResourceWithNotifications;
import com.fuzzy.subsystems.utils.DomainObjectValidator;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;

public class RCTagImpl extends AbstractQueryRController<CoreSubsystem> implements RCTag {


    private final RemovableResourceWithNotifications<TagEditable, RCTagNotifications> tagEditableResource;

    public RCTagImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        tagEditableResource = new RemovableResourceWithNotifications<>(resources, TagEditable.class, RCTagNotifications.class);
    }

    @Override
    public TagReadable create(TagBuilder tagBuilder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!tagBuilder.isContainName()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(TagReadable.class, TagReadable.FIELD_NAME);
        }
        if (!tagBuilder.isContainColour() || StringUtils.isEmpty(tagBuilder.getColour())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(TagReadable.class, TagReadable.FIELD_COLOUR);
        }
        if (!tagBuilder.isContainReadOnly()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(TagReadable.class, TagReadable.FIELD_READ_ONLY);
        }
        validateNonEmptyAndUniqueName(tagBuilder.getName(), null, transaction);
        TagEditable tag = tagEditableResource.create(transaction);
        setFieldsFor(tag, tagBuilder);
        tagEditableResource.saveCreation(tag, context);
        return tag;
    }

    @Override
    public TagReadable update(long tagId, TagBuilder tagBuilder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        TagEditable tag = new PrimaryKeyValidator(false)
                .validateAndGet(tagId, tagEditableResource, transaction);
        if (tag.isReadOnly()) {
            throw CoreExceptionBuilder.buildReadOnlyTagException(tagId);
        }
        if (tagBuilder.isContainName()) {
            validateNonEmptyAndUniqueName(tagBuilder.getName(), tagId, transaction);
        }
        if (tagBuilder.isContainColour() && StringUtils.isEmpty(tagBuilder.getColour())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(TagReadable.class, TagReadable.FIELD_COLOUR);
        }
        if (tagBuilder.isContainReadOnly()) {
            throw GeneralExceptionBuilder.buildInvalidValueException(TagReadable.class, TagReadable.FIELD_READ_ONLY, tagBuilder.getReadOnly());
        }
        setFieldsFor(tag, tagBuilder);
        tagEditableResource.saveUpdate(tag, context);
        return tag;
    }

    @Override
    public HashSet<Long> remove(HashSet<Long> tagIds, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashSet<Long> removed = new HashSet<>();
        for (Long tagId : tagIds) {
            if (tagId == null) {
                continue;
            }
            TagEditable tag = tagEditableResource.get(tagId, transaction);
            if (tag != null && !tag.isReadOnly()) {
                tagEditableResource.remove(tag, context);
                removed.add(tagId);
            }
        }
        return removed;
    }

    private void validateNonEmptyAndUniqueName(String name, Long excludedId, QueryTransaction transaction)
            throws PlatformException {
        DomainObjectValidator.validateNonEmptyAndUnique(
                TagReadable.FIELD_NAME,
                name,
                excludedId,
                tagEditableResource,
                transaction
        );
    }

    private void setFieldsFor(TagEditable tag, TagBuilder tagBuilder) {
        if (tagBuilder.isContainName()) {
            tag.setName(tagBuilder.getName());
        }
        if (tagBuilder.isContainColour()) {
            tag.setColour(tagBuilder.getColour());
        }
        if (tagBuilder.isContainReadOnly()) {
            tag.setReadOnly(tagBuilder.getReadOnly());
        }
    }
}
