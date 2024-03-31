package com.fuzzy.subsystem.core.domainobject.employeephone;

import com.fuzzy.database.domainobject.DomainObjectEditable;

public class EmployeePhoneEditable extends EmployeePhoneReadable implements DomainObjectEditable {

	public EmployeePhoneEditable(long id) {
		super(id);
	}

	public void setEmployeeId(long employeeId) {
		set(FIELD_EMPLOYEE_ID, employeeId);
	}

	public void setPhoneNumber(String phoneNumber) {
		set(FIELD_PHONE_NUMBER, phoneNumber);
	}
}
