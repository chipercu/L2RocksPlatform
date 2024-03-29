package com.fuzzy.subsystem.core.entityprovider.datasources;

public enum EmployeeEntityFields {

    ID("id"),
    FIRST_NAME("first_name"),
    SECOND_NAME("second_name"),
    PATRONYMIC("patronymic"),
    LOGIN("login"),
    EMAIL("email"),
    AUTHENTICATION_ASSIGNED("authentication_assigned"),
    MONITORING_TYPE("monitoring_type"),
    ALL_EMPLOYEE_ACCESS("all_employee_access"),
    EMPLOYEE_ACCOUNT_IDS("employee_account_ids"),
    ACCESS_ROLE_IDS("access_role_ids"),
    ACCESS_TO_EMPLOYEE_IDS("access_to_employee_ids"),
    ACCESS_TO_DEPARTMENT_IDS("access_to_department_ids"),
    DEPARTMENTS("departments"),
    DEPARTMENT_IDS("department_ids"),
    ADDITIONAL_FIELDS_NAMES("additional_fields_names"),
    ADDITIONAL_FIELDS_VALUES("additional_fields_values"),
    PHONES("phones"),
    NAME("name"),
    PERSONNEL_NUMBER("personnel_number"),
    EMPLOYEE_ACCOUNT_LOGINS("employee_account_logins"),
    EMPLOYEE_ACCOUNT_DOMAINS("employee_account_domains");

    private final String fieldName;

    EmployeeEntityFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static EmployeeEntityFields get(String value) {
        for (EmployeeEntityFields fields : EmployeeEntityFields.values()) {
            if (fields.getFieldName().equals(value)) {
                return fields;
            }
        }
        return null;
    }
}
