package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.modelspace.BuilderFields;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.HashSet;

public class EmployeeBuilder extends BuilderFields {

    private HashSet<String> phoneNumbers;
    private transient GOptional<Instant> lastLogonTime = GOptional.notPresent();

    public EmployeeBuilder withEmail(String value) {
        fields.put(EmployeeReadable.FIELD_EMAIL, value);
        return this;
    }

    public EmployeeBuilder withLogin(String value) {
        fields.put(EmployeeReadable.FIELD_LOGIN, value);
        return this;
    }

    public EmployeeBuilder withFirstName(String value) {
        fields.put(EmployeeReadable.FIELD_FIRST_NAME, value);
        return this;
    }

    public EmployeeBuilder withPatronymic(String value) {
        fields.put(EmployeeReadable.FIELD_PATRONYMIC, value);
        return this;
    }

    public EmployeeBuilder withSecondName(String value) {
        fields.put(EmployeeReadable.FIELD_SECOND_NAME, value);
        return this;
    }

    public EmployeeBuilder withPasswordHash(String passwordHash) {
        fields.put(EmployeeReadable.FIELD_PASSWORD_HASH, passwordHash);
        return this;
    }

    public EmployeeBuilder withDepartmentId(Long departmentId) {
        fields.put(EmployeeReadable.FIELD_DEPARTMENT_ID, departmentId);
        return this;
    }

    public EmployeeBuilder withLanguage(@NonNull Language language) {
        if (language == null) {
            throw new IllegalArgumentException("language cannot be null");
        }
        fields.put(EmployeeReadable.FIELD_LANGUAGE, language);
        return this;
    }

    public EmployeeBuilder withPersonnelNumber(String personnelNumber) {
        fields.put(EmployeeReadable.FIELD_PERSONNEL_NUMBER, personnelNumber);
        return this;
    }

    public EmployeeBuilder withNeedToChangePassword(@NonNull Boolean value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        fields.put(EmployeeReadable.FIELD_NEED_TO_CHANGE_PASSWORD, value);
        return this;
    }

    public EmployeeBuilder withLastLogonTime(@NonNull Instant value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        lastLogonTime = GOptional.of(value);
        return this;
    }

    public EmployeeBuilder withPhoneNumbers(@NonNull HashSet<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    public EmployeeBuilder withSendSystemEvents(@NonNull Boolean value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        fields.put(EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS, value);
        return this;
    }


    public boolean isContainEmail() {
        return fields.containsKey(EmployeeReadable.FIELD_EMAIL);
    }

    public boolean isContainLogin() {
        return fields.containsKey(EmployeeReadable.FIELD_LOGIN);
    }

    public boolean isContainFirstName() {
        return fields.containsKey(EmployeeReadable.FIELD_FIRST_NAME);
    }

    public boolean isContainPatronymic() {
        return fields.containsKey(EmployeeReadable.FIELD_PATRONYMIC);
    }

    public boolean isContainSecondName() {
        return fields.containsKey(EmployeeReadable.FIELD_SECOND_NAME);
    }

    public boolean isContainPasswordHash() {
        return fields.containsKey(EmployeeReadable.FIELD_PASSWORD_HASH);
    }

    public boolean isContainDepartmentId() {
        return fields.containsKey(EmployeeReadable.FIELD_DEPARTMENT_ID);
    }

    public boolean isContainLanguage() {
        return fields.containsKey(EmployeeReadable.FIELD_LANGUAGE);
    }

    public boolean isContainPersonnelNumber() {
        return fields.containsKey(EmployeeReadable.FIELD_PERSONNEL_NUMBER);
    }

    public boolean isContainNeedToChangePassword() {
        return fields.containsKey(EmployeeReadable.FIELD_NEED_TO_CHANGE_PASSWORD);
    }

    public boolean isContainLastLogonTime() {
        return lastLogonTime.isPresent();
    }

    public boolean isContainPhoneNumbers() {
        return phoneNumbers != null;
    }

    public boolean isSendSystemEvents() {
        return fields.containsKey(EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS);
    }

    public String getEmail() {
        return (String) fields.get(EmployeeReadable.FIELD_EMAIL);
    }

    public String getLogin() {
        return (String) fields.get(EmployeeReadable.FIELD_LOGIN);
    }

    public String getFirstName() {
        return (String) fields.get(EmployeeReadable.FIELD_FIRST_NAME);
    }

    public String getPatronymic() {
        return (String) fields.get(EmployeeReadable.FIELD_PATRONYMIC);
    }

    public String getSecondName() {
        return (String) fields.get(EmployeeReadable.FIELD_SECOND_NAME);
    }

    public String getPasswordHash() {
        return (String) fields.get(EmployeeReadable.FIELD_PASSWORD_HASH);
    }

    public Long getDepartmentId() {
        return (Long) fields.get(EmployeeReadable.FIELD_DEPARTMENT_ID);
    }

    public Language getLanguage() {
        return (Language) fields.get(EmployeeReadable.FIELD_LANGUAGE);
    }

    public String getPersonnelNumber() {
        return (String) fields.get(EmployeeReadable.FIELD_PERSONNEL_NUMBER);
    }

    public Boolean getNeedToChangePassword() {
        return (Boolean) fields.get(EmployeeReadable.FIELD_NEED_TO_CHANGE_PASSWORD);
    }

    public Instant getLastLogonTime() {
        return lastLogonTime.get();
    }

    public HashSet<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Boolean getSendSystemEvents() {
        return (Boolean) fields.get(EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS);
    }
}
