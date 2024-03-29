package com.fuzzy.subsystem.core.securitylog;

public class CoreParameter {

    public static class Employee {
        public static final String ID = "id";
        public static final String LOGIN = "login";
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "first_name";
        public static final String SECOND_NAME = "second_name";
        public static final String PATRONYMIC = "patronymic";
        public static final String PERSONNEL_NUMBER = "personnel_number";
        public static final String OLD_FIRST_NAME = "old_first_name";
        public static final String OLD_SECOND_NAME = "old_second_name";
        public static final String OLD_PATRONYMIC = "old_patronymic";
        public static final String OLD_PERSONNEL_NUMBER = "old_personnel_number";
        public static final String OLD_LOGIN = "old_login";
        public static final String OLD_EMAIL = "old_email";
        public static final String OLD_DEPARTMENT_ID = "old_department_id";
        public static final String NEW_FIRST_NAME = "new_first_name";
        public static final String NEW_SECOND_NAME = "new_second_name";
        public static final String NEW_PATRONYMIC = "new_patronymic";
        public static final String NEW_PERSONNEL_NUMBER = "new_personnel_number";
        public static final String NEW_LOGIN = "new_login";
        public static final String NEW_EMAIL = "new_email";
        public static final String NEW_DEPARTMENT_ID = "new_department_id";
        public static final String STATUS = "status";
        public static final String SESSION_HASH = "session_hash";
        public static final String CAUSE = "cause";
        public static final String OLD_VALUE = "old_value";
        public static final String NEW_VALUE = "new_value";
        public static final String ALL = "all";
        public static final String ACCESS_ROLE_ID = "access_role_id";
        public static final String ACCESS_ROLE_NAME = "access_role_name";
        public static final String EMPLOYEE_ID = "employee_id";
        public static final String MERGING_EMPLOYEE_ID = "merging_employee_id";
        public static final String MERGING_EMPLOYEE_LOGIN = "merging_employee_login";
    }

    public static class Department {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String OLD_NAME = "old_name";
        public static final String NEW_NAME = "new_name";
    }

    public static class Database {
        public static final String STATUS = "status";
    }

    public static class ApiKey {
        public static final String STATUS = "status";
        public static final String CAUSE = "cause";
        public static final String ID = "id";
        public static final String API_KEY_ID = "api_key_id";
        public static final String NAME = "name";
        public static final String SESSION_HASH = "session_hash";

    }

    public static class Authentication {
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String NEW_NAME = "new_name";
        public static final String OLD_NAME = "old_name";
        public static final String OLD_VALUE = "old_value";
        public static final String NEW_VALUE = "new_value";
    }

    public static class License {
        public static final String ID = "id";
        public static final String LICENSE_KEY = "license_key";
    }
}
