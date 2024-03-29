package com.fuzzy.subsystem.core.entityprovider.entity;

import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.entityprovider.datasources.TagDataSource;
import com.infomaximum.subsystem.entityprovidersdk.entity.DataContainer;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityClass;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityField;
import com.infomaximum.subsystem.entityprovidersdk.entity.Id;
import com.infomaximum.subsystem.entityprovidersdk.enums.DataType;

@EntityClass(
        name = "tag",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = TagDataSource.class)
public class TagEntity implements DataContainer {

    private final TagReadable tagReadable;

    public TagEntity(TagReadable tagReadable) {
        this.tagReadable = tagReadable;
    }

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return tagReadable.getId();
    }

    @EntityField(name = "name", type = DataType.STRING)
    public String getName() {
        return tagReadable.getName();
    }

    @EntityField(name = "colour", type = DataType.STRING)
    public String getColour() {
        return tagReadable.getColour();
    }

    @EntityField(name = "read_only", type = DataType.BOOLEAN)
    public Boolean getReadOnly() {
        return tagReadable.isReadOnly();
    }
}
