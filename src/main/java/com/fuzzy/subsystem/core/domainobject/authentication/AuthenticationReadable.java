package com.fuzzy.subsystem.core.domainobject.authentication;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.main.rdao.database.anotation.PrefixIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "Authentication",
        fields = {
                @Field(name = "name", number = AuthenticationReadable.FIELD_NAME, type = String.class),
                @Field(name = "type", number = AuthenticationReadable.FIELD_TYPE, type = String.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AuthenticationReadable.FIELD_NAME }),
                @HashIndex(fields = { AuthenticationReadable.FIELD_TYPE })
        },
        prefixIndexes = {
                @PrefixIndex(fields = { AuthenticationReadable.FIELD_NAME })
        }
)
public class AuthenticationReadable extends RDomainObject {

    public final static int FIELD_NAME = 0;
    public final static int FIELD_TYPE = 1;

    public AuthenticationReadable(long id) {
        super(id);
    }

    public String getName() {
        return getString(FIELD_NAME);
    }

    public String getType() {
        return getString(FIELD_TYPE);
    }
}