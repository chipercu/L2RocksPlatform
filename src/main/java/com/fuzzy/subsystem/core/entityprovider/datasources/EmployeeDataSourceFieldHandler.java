package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.fuzzy.subsystem.entityprovidersdk.data.EntityFieldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeDataSourceFieldHandler {

    private final List<EntityFieldInfo> fields;
    private final Set<EmployeeEntityFields> entityFields;

    public EmployeeDataSourceFieldHandler(List<EntityFieldInfo> fields) {
        this.fields = fields;
        entityFields = initFieldSet();
    }


    private Set<EmployeeEntityFields> initFieldSet() {
        if (Objects.isNull(fields) || fields.isEmpty()) {
            return Arrays.stream(EmployeeEntityFields.values())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        return fields.stream()
                .map(entityFieldInfo -> EmployeeEntityFields.get(entityFieldInfo.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public boolean isId() {
        return entityFields.contains(EmployeeEntityFields.ID);
    }

    public boolean isFirstName() {
        return entityFields.contains(EmployeeEntityFields.FIRST_NAME);
    }

    public boolean isSecondName() {
        return entityFields.contains(EmployeeEntityFields.SECOND_NAME);
    }

    public boolean isPatronymic() {
        return entityFields.contains(EmployeeEntityFields.PATRONYMIC);
    }

    public boolean isLogin() {
        return entityFields.contains(EmployeeEntityFields.LOGIN);
    }

    public boolean isEmail() {
        return entityFields.contains(EmployeeEntityFields.EMAIL);
    }

    public boolean isAuthenticationAssigned() {
        return entityFields.contains(EmployeeEntityFields.AUTHENTICATION_ASSIGNED);
    }

    public boolean isMonitoringType() {
        return entityFields.contains(EmployeeEntityFields.MONITORING_TYPE);
    }

    public boolean isAllEmployeeAccess() {
        return entityFields.contains(EmployeeEntityFields.ALL_EMPLOYEE_ACCESS);
    }

    public boolean isEmployeeAccountIds() {
        return entityFields.contains(EmployeeEntityFields.EMPLOYEE_ACCOUNT_IDS);
    }

    public boolean isAccessRoleIds() {
        return entityFields.contains(EmployeeEntityFields.ACCESS_ROLE_IDS);
    }

    public boolean isAccessToEmployeeIds() {
        return entityFields.contains(EmployeeEntityFields.ACCESS_TO_EMPLOYEE_IDS);
    }

    public boolean isAccessToDepartmentIds() {
        return entityFields.contains(EmployeeEntityFields.ACCESS_TO_DEPARTMENT_IDS);
    }

    public boolean isDepartments() {
        return entityFields.contains(EmployeeEntityFields.DEPARTMENTS);
    }

    public boolean isDepartmentIds() {
        return entityFields.contains(EmployeeEntityFields.DEPARTMENT_IDS);
    }

    public boolean isAdditionalFieldsNames() {
        return entityFields.contains(EmployeeEntityFields.ADDITIONAL_FIELDS_NAMES);
    }

    public boolean isAdditionalFieldsValues() {
        return entityFields.contains(EmployeeEntityFields.ADDITIONAL_FIELDS_VALUES);
    }

    public boolean isPhones() {
        return entityFields.contains(EmployeeEntityFields.PHONES);
    }

    public boolean isName() {
        return entityFields.contains(EmployeeEntityFields.NAME);
    }

    public boolean isPersonnelNumber() {
        return entityFields.contains(EmployeeEntityFields.PERSONNEL_NUMBER);
    }

    public boolean isAccountLogins() {
        return entityFields.contains(EmployeeEntityFields.EMPLOYEE_ACCOUNT_LOGINS);
    }

    public boolean isAccountDomains() {
        return entityFields.contains(EmployeeEntityFields.EMPLOYEE_ACCOUNT_DOMAINS);
    }
}