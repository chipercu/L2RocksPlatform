package com.fuzzy.subsystem.core.domainobject.employee;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.utils.Random;

public class EmployeeEditable extends EmployeeReadable implements DomainObjectEditable {

    public EmployeeEditable(long id) {
        super(id);
    }

    public void setDepartmentId(Long departmentId) {
        set(FIELD_DEPARTMENT_ID, departmentId);
    }

    public void setEmail(String email) {
        set(FIELD_EMAIL, email);
    }

    public void setLogin(String login) {
        set(FIELD_LOGIN, login);
    }

    public void setFirstName(String firstName) {
        set(FIELD_FIRST_NAME, firstName);
    }

    public void setPatronymic(String patronymic) {
        set(FIELD_PATRONYMIC, patronymic);
    }

    public void setSecondName(String secondName) {
        set(FIELD_SECOND_NAME, secondName);
    }

    public void setPasswordHashWithSalt(String rawPasswordHash) {
        byte[] salt = null;
        if (rawPasswordHash != null) {
            salt = new byte[16];
            Random.secureRandom.nextBytes(salt);
        }
        set(FIELD_SALT, salt);
        set(FIELD_PASSWORD_HASH, getSaltyPasswordHash(rawPasswordHash, salt));
    }

    public void setLanguage(Language language) {
        set(FIELD_LANGUAGE, language);
    }

    public void setPersonnelNumber(String personnelNumber) {
        set(FIELD_PERSONNEL_NUMBER, personnelNumber);
    }

    public void setNeedToChangePassword(boolean needToChangePassword) {
        set(FIELD_NEED_TO_CHANGE_PASSWORD, needToChangePassword);
    }

    public void setSendSystemEvents(boolean isSendSystemEvents) {
        set(FIELD_SEND_SYSTEM_EVENTS, isSendSystemEvents);
    }
}
