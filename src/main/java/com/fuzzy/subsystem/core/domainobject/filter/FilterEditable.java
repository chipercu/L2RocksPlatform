package com.fuzzy.subsystem.core.domainobject.filter;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class FilterEditable extends FilterReadable implements DomainObjectEditable {

	public FilterEditable(long id) {
		super(id);
	}

	public void setEmployeeId(Long employeeId) {
		set(FIELD_EMPLOYEE_ID, employeeId);
	}

	public void setGroup(String group) {
		set(FIELD_GROUP, group);
	}

	public void setName(String name) {
		set(FIELD_NAME, name);
	}

	public void setValue(String value) {
		set(FIELD_VALUE, value);
	}
}
