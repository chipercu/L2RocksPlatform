package com.fuzzy.subsystem.core.domainobject.config;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.domainobject.config.ConfigReadable;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "Config",
        fields = {
                @Field(name = "name", number = ConfigReadable.FIELD_NAME, type = String.class),
                @Field(name = "value", number = ConfigReadable.FIELD_VALUE, type = byte[].class)
        },
        hashIndexes = {
                @HashIndex(fields = {ConfigReadable.FIELD_NAME})
        }
)
public class CoreConfigReadable extends ConfigReadable {

    public CoreConfigReadable(long id) {
        super(id);
    }
}
