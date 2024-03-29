package com.fuzzy.subsystem.core.domainobject.employeephone;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
		namespace = CoreSubsystemConsts.UUID,
		name = "EmployeePhone",
		fields = {
				@Field(name = "employee_id", number = EmployeePhoneReadable.FIELD_EMPLOYEE_ID,
						type = Long.class, foreignDependency = EmployeeReadable.class),
				@Field(name = "phone_number", number = EmployeePhoneReadable.FIELD_PHONE_NUMBER,
						type = String.class)
		},
		hashIndexes = {
				@HashIndex(fields = { EmployeePhoneReadable.FIELD_EMPLOYEE_ID }),
				@HashIndex(fields = { EmployeePhoneReadable.FIELD_PHONE_NUMBER })
		}
)
public class EmployeePhoneReadable extends RDomainObject {

	public final static int FIELD_EMPLOYEE_ID = 0;
	public final static int FIELD_PHONE_NUMBER = 1;

	public EmployeePhoneReadable(long id) {
		super(id);
	}

	public long getEmployeeId() {
		return get(FIELD_EMPLOYEE_ID);
	}

	public String getPhoneNumber() {
		return get(FIELD_PHONE_NUMBER);
	}
}