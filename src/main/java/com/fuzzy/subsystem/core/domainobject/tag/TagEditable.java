package com.fuzzy.subsystem.core.domainobject.tag;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public class TagEditable extends TagReadable implements DomainObjectEditable {

	public TagEditable(long id) {
		super(id);
	}

	public void setName(String name) {
		set(FIELD_NAME, name);
	}

	public void setColour(String colour) {
		set(FIELD_COLOUR, colour);
	}

	public void setReadOnly(boolean readOnly) {
		set(FIELD_READ_ONLY, readOnly);
	}
}