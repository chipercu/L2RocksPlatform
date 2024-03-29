package com.fuzzy.subsystem.core.remote.tag;

import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystems.modelspace.BuilderFields;

public class TagBuilder extends BuilderFields {

	public TagBuilder withName(String value) {
		fields.put(TagReadable.FIELD_NAME, value);
		return this;
	}

	public TagBuilder withColour(String colour) {
		fields.put(TagReadable.FIELD_COLOUR, colour);
		return this;
	}

	public TagBuilder withReadOnly(boolean readOnly) {
		fields.put(TagReadable.FIELD_READ_ONLY, readOnly);
		return this;
	}

	public boolean isContainName() {
		return fields.containsKey(TagReadable.FIELD_NAME);
	}

	public boolean isContainColour() {
		return fields.containsKey(TagReadable.FIELD_COLOUR);
	}

	public boolean isContainReadOnly() {
		return fields.containsKey(TagReadable.FIELD_READ_ONLY);
	}

	public String getName() {
		return (String) fields.get(TagReadable.FIELD_NAME);
	}

	public String getColour() {
		return (String) fields.get(TagReadable.FIELD_COLOUR);
	}

	public boolean getReadOnly() {
		return (boolean) fields.get(TagReadable.FIELD_READ_ONLY);
	}
}