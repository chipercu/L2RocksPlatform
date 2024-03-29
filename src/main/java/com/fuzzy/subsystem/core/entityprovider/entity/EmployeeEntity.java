package com.fuzzy.subsystem.core.entityprovider.entity;

import com.google.common.collect.Lists;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.entityprovider.datasources.EmployeeDataSource;
import com.infomaximum.subsystem.entityprovidersdk.entity.DataContainer;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityClass;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityField;
import com.infomaximum.subsystem.entityprovidersdk.entity.Id;
import com.infomaximum.subsystem.entityprovidersdk.enums.DataType;

import java.util.Collection;

@EntityClass(
        name = "employee",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = EmployeeDataSource.class
)
public class EmployeeEntity implements DataContainer {
    private final Long id;
    private final String firstName;
    private final String secondName;
    private final String patronymic;
    private final Boolean authenticationAssigned;
    private final String monitoringType;
    private final Boolean allEmployeeAccess;
    private final Collection<Long> employeeAccountIds;
    private final Collection<Long> accessRoleIds;
    private final Collection<Long> accessToEmployeeIds;
    private Collection<Long> accessToDepartmentIds;
    private final Collection<String> departments;
    private final Collection<Long> departmentIds;
    private final Collection<String> additionalFieldsNames;
    private final Collection<String> additionalFieldsValues;
    private final String login;
    private final String email;
    private final Collection<String> phones;
    private final String name;
    private final String personnelNumber;
    private final Collection<String> accountLogins;
    private final Collection<String> accountDomains;

    private EmployeeEntity(Builder builder) {
        id = builder.id;
        firstName = builder.firstName;
        secondName = builder.secondName;
        patronymic = builder.patronymic;
        authenticationAssigned = builder.authenticationAssigned;
        monitoringType = builder.monitoringType;
        allEmployeeAccess = builder.allEmployeeAccess;
        employeeAccountIds = builder.employeeAccountIds;
        accessRoleIds = builder.accessRoleIds;
        accessToEmployeeIds = builder.accessToEmployeeIds;
        departments = builder.departments;
        departmentIds = builder.departmentIds;
        additionalFieldsNames = builder.additionalFieldsNames;
        additionalFieldsValues = builder.additionalFieldsValues;
        login = builder.login;
        email = builder.email;
        phones = builder.phones;
        name = builder.name;
        personnelNumber = builder.personnelNumber;
        accountLogins = builder.employeeAccountLogins;
        accountDomains = builder.employeeAccountDomains;
        accessToDepartmentIds = builder.accessToDepartmentIds;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return id;
    }

    @EntityField(name = "first_name", type = DataType.STRING)
    public String getFirstName() {
        return firstName;
    }

    @EntityField(name = "second_name", type = DataType.STRING)
    public String getSecondName() {
        return secondName;
    }

    @EntityField(name = "patronymic", type = DataType.STRING)
    public String getPatronymic() {
        return patronymic;
    }

    @EntityField(name = "login", type = DataType.STRING)
    public String getLogin() {
        return login;
    }

    @EntityField(name = "email", type = DataType.STRING)
    public String getEmail() {
        return email;
    }

    @EntityField(name = "authentication_assigned", type = DataType.BOOLEAN)
    public Boolean isAuthenticationAssigned() {
        return authenticationAssigned;
    }

    @EntityField(name = "monitoring_type", type = DataType.STRING)
    public String getMonitoringType() {
        return monitoringType;
    }

    @EntityField(name = "all_employee_access", type = DataType.BOOLEAN)
    public Boolean isAllEmployeeAccess() {
        return allEmployeeAccess;
    }

    @EntityField(name = "employee_account_ids", type = DataType.LONG_ARRAY)
    public Collection<Long> getEmployeeAccountIds() {
        return employeeAccountIds;
    }

    @EntityField(name = "access_role_ids", type = DataType.LONG_ARRAY)
    public Collection<Long> getAccessRoleIds() {
        return accessRoleIds;
    }

    @EntityField(name = "access_to_employee_ids", type = DataType.LONG_ARRAY)
    public Collection<Long> getAccessToEmployeeIds() {
        return accessToEmployeeIds;
    }

    @EntityField(name = "access_to_department_ids", type = DataType.LONG_ARRAY)
    public Collection<Long> getAccessToDepartmentIds() {
        return accessToDepartmentIds;
    }

    @EntityField(name = "departments", type = DataType.STRING_ARRAY)
    public Collection<String> getDepartments() {
        return departments;
    }

    @EntityField(name = "department_ids", type = DataType.LONG_ARRAY)
    public Collection<Long> getDepartmentIds() {
        return departmentIds;
    }

    @EntityField(name = "additional_fields_names", type = DataType.STRING_ARRAY)
    public Collection<String> getAdditionalFieldsNames() {
        return additionalFieldsNames;
    }

    @EntityField(name = "additional_fields_values", type = DataType.STRING_ARRAY)
    public Collection<String> getAdditionalFieldsValues() {
        return additionalFieldsValues;
    }

    @EntityField(name = "phones", type = DataType.STRING_ARRAY)
    public Collection<String> getPhones() {
        return phones;
    }

    @EntityField(name = "name", type = DataType.STRING)
    public String getName() {
        return name;
    }

    @EntityField(name = "personnel_number", type = DataType.STRING)
    public String getPersonnelNumber() {
        return personnelNumber;
    }

    @EntityField(name = "employee_account_logins", type = DataType.STRING_ARRAY)
    public Collection<String> getAccountLogins() {
        return accountLogins;
    }

    @EntityField(name = "employee_account_domains", type = DataType.STRING_ARRAY)
    public Collection<String> getAccountDomains() {
        return accountDomains;
    }


    public static final class Builder {
        private Long id;
        private String firstName;
        private String secondName;
        private String patronymic;
        private Boolean authenticationAssigned;
        private String monitoringType;
        private Boolean allEmployeeAccess;
        private Collection<Long> employeeAccountIds = Lists.newArrayList();
        private Collection<Long> accessRoleIds = Lists.newArrayList();
        private Collection<Long> accessToEmployeeIds = Lists.newArrayList();
        private Collection<Long> accessToDepartmentIds = Lists.newArrayList();
        private Collection<String> departments = Lists.newArrayList();
        private Collection<Long> departmentIds = Lists.newArrayList();
        private Collection<String> additionalFieldsNames = Lists.newArrayList();
        private Collection<String> additionalFieldsValues = Lists.newArrayList();
        private String login;
        private String email;
        private Collection<String> phones = Lists.newArrayList();
        public String name;
        private String personnelNumber;
        private Collection<String> employeeAccountLogins = Lists.newArrayList();
        private Collection<String> employeeAccountDomains = Lists.newArrayList();

        private Builder() {
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setSecondName(String secondName) {
            this.secondName = secondName;
            return this;
        }

        public Builder setPatronymic(String patronymic) {
            this.patronymic = patronymic;
            return this;
        }

        public Builder setAuthenticationAssigned(Boolean authenticationAssigned) {
            this.authenticationAssigned = authenticationAssigned;
            return this;
        }

        public Builder setMonitoringType(String monitoringType) {
            this.monitoringType = monitoringType;
            return this;
        }

        public Builder setAllEmployeeAccess(Boolean allEmployeeAccess) {
            this.allEmployeeAccess = allEmployeeAccess;
            return this;
        }

        public Builder addEmployeeAccountId(Long employeeAccountId) {
            this.employeeAccountIds.add(employeeAccountId);
            return this;
        }

        public Builder addAccessRoleIds(Long accessRoleId) {
            this.accessRoleIds.add(accessRoleId);
            return this;
        }

        public Builder addAccessToEmployeeIds(Long accessToEmployeeId) {
            this.accessToEmployeeIds.add(accessToEmployeeId);
            return this;
        }

        public Builder addAccessToDepartmentIds(Long accessToDepartmentId) {
            this.accessToDepartmentIds.add(accessToDepartmentId);
            return this;
        }

        public Builder addDepartment(String departmentName) {
            this.departments.add(departmentName);
            return this;
        }

        public Builder addDepartmentId(Long departmentId) {
            this.departmentIds.add(departmentId);
            return this;
        }

        public Builder addAdditionalFieldsName(String fieldName) {
            this.additionalFieldsNames.add(fieldName);
            return this;
        }

        public Builder addAdditionalFieldsValue(String fieldValue) {
            this.additionalFieldsValues.add(fieldValue);
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder addPhone(String phone) {
            this.phones.add(phone);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPersonnelNumber(String personnelNumber) {
            this.personnelNumber = personnelNumber;
            return this;
        }

        public Builder addEmployeeAccountLogin(String accountLogin) {
            this.employeeAccountLogins.add(accountLogin);
            return this;
        }

        public Builder addEmployeeAccountDomain(String accountDomain) {
            this.employeeAccountDomains.add(accountDomain);
            return this;
        }


        public EmployeeEntity build() {
            return new EmployeeEntity(this);
        }
    }
}