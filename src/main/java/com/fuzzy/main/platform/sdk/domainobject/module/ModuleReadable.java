package com.fuzzy.main.platform.sdk.domainobject.module;

import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.exception.DatabaseException;

@Entity(
        namespace = "com.fuzzy.subsystems",
        name = "Module",
        fields = {
                @Field(name = "uuid", number = ModuleReadable.FIELD_UUID, type = String.class),
                @Field(name = "version", number = ModuleReadable.FIELD_VERSION, type = String.class)
        },
        hashIndexes = {
                @HashIndex(fields = {ModuleReadable.FIELD_UUID})
        }
)
public class ModuleReadable extends DomainObject {

    public final static int FIELD_UUID = 0;
    public final static int FIELD_VERSION = 1;

    public ModuleReadable(long id) {
        super(id);
    }

    public String getUuid() throws DatabaseException {
        return getString(FIELD_UUID);
    }

    public Version getVersion() throws DatabaseException {
        return Version.parse(getString(FIELD_VERSION));
    }
}
