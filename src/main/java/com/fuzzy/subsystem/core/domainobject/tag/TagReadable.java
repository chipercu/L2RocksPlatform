package com.fuzzy.subsystem.core.domainobject.tag;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.anotation.Field;
import com.fuzzy.database.anotation.HashIndex;
import com.fuzzy.database.anotation.PrefixIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
		namespace = CoreSubsystemConsts.UUID,
		name = "Tag",
		fields = {
				@Field(name = "name", number = TagReadable.FIELD_NAME, type = String.class),
				@Field(name = "colour", number = TagReadable.FIELD_COLOUR, type = String.class),
				@Field(name = "read_only", number = TagReadable.FIELD_READ_ONLY, type = Boolean.class)
		},
		hashIndexes = {
				@HashIndex(fields = { TagReadable.FIELD_NAME })
		},
		prefixIndexes = {
				@PrefixIndex(fields = { TagReadable.FIELD_NAME })
		}
)
public class TagReadable extends RDomainObject {

	public final static int FIELD_NAME = 0;
	public final static int FIELD_COLOUR = 1;
	public final static int FIELD_READ_ONLY = 2;

	public TagReadable(long id) {
		super(id);
	}

	public String getName() {
		return getString(FIELD_NAME);
	}

	public String getColour() {
		return getString(FIELD_COLOUR);
	}

	public boolean isReadOnly() {
		Boolean readOnly = get(FIELD_READ_ONLY);
		return readOnly != null ? readOnly : false;
	}
}