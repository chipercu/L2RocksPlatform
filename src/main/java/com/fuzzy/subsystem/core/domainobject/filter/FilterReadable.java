package com.fuzzy.subsystem.core.domainobject.filter;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
		namespace = CoreSubsystemConsts.UUID,
		name = "Filter",
		fields = {
				@Field(name = "employee_id", number = FilterReadable.FIELD_EMPLOYEE_ID,
						type = Long.class, foreignDependency = EmployeeReadable.class),
				@Field(name = "group", number = FilterReadable.FIELD_GROUP, type = String.class),
				@Field(name = "name", number = FilterReadable.FIELD_NAME, type = String.class),
				@Field(name = "value", number = FilterReadable.FIELD_VALUE, type = String.class)
		},
		hashIndexes = {
				@HashIndex(fields = { FilterReadable.FIELD_EMPLOYEE_ID, FilterReadable.FIELD_GROUP })
		}
)
public class FilterReadable extends RDomainObject {

	public final static int FIELD_EMPLOYEE_ID = 0;
	public final static int FIELD_GROUP = 1;
	public final static int FIELD_NAME = 2;
	public final static int FIELD_VALUE = 3;

	public FilterReadable(long id) {
		super(id);
	}

	public Long getEmployeeId() {
		return getLong(FIELD_EMPLOYEE_ID);
	}

	public String getGroup() {
		return getString(FIELD_GROUP);
	}

	public String getName() {
		return getString(FIELD_NAME);
	}

	public String getValue() {
		return getString(FIELD_VALUE);
	}
}
