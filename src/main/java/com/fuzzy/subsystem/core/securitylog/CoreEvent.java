package com.fuzzy.subsystem.core.securitylog;

public class CoreEvent {

    public static class System {
        public static final String TYPE_INITIALIZE = "initialize";
        public static final String TYPE_START = "start";
        public static final String TYPE_STOP = "stop";
        public static final String TYPE_CRUSH = "crush";
    }

    public static class Employee {
        public static final String TYPE_CREATE = "create";
        public static final String TYPE_UPDATE = "update";
        public static final String TYPE_REMOVE = "remove";
        public static final String TYPE_MERGE = "merge";
        public static final String TYPE_LOGON = "logon";
        public static final String TYPE_LOGOUT = "logout";
        public static final String TYPE_CHANGE_PASSWORD = "change_password";
        public static final String TYPE_ADDING_ACCESS_ROLE = "adding_access_role";
        public static final String TYPE_REMOVING_ACCESS_ROLE = "removing_access_role";
        public static final String TYPE_ADDING_ACCESS_TO_EMPLOYEE = "adding_access_to_employee";
        public static final String TYPE_REMOVING_ACCESS_TO_EMPLOYEE = "removing_access_to_employee";
        public static final String TYPE_CHANGE_ENABLED_MONITORING = "change_enabled_monitoring";
    }

    public static class Database {
        public static final String TYPE_INTEGRITY_CHECK = "integrity_check";
    }

    public static class ApiKey {
        public static final String TYPE_AUTH = "auth";
    }

    public static class Department {
        public static final String TYPE_CREATE = "create";
        public static final String TYPE_UPDATE = "update";
        public static final String TYPE_REMOVE = "remove";
    }

    //todo V.Bukharkin refactoring
    public static final String TYPE_CHANGE_PRIVILEGE = "change_privilege";
    public static final String PARAM_PRIVILEGE = "privilege";
    public static final String PARAM_OLD_OPERATIONS = "old_operations";
    public static final String PARAM_NEW_OPERATIONS = "new_operations";

    public static final class Authentication {
        public static final String TYPE_CREATE = "create";
        public static final String TYPE_UPDATE = "update";
        public static final String TYPE_REMOVE = "remove";
        public static final String TYPE_CHANGE_COMPLEX_PASSWORD = "change_complex_password";
        public static final String TYPE_CHANGE_MIN_PASSWORD_LENGTH = "change_min_password_length";
        public static final String TYPE_CHANGE_PASSWORD_EXPIRATION_TIME = "change_password_expiration_time";
        public static final String TYPE_CHANGE_MAX_INVALID_LOGON_COUNT = "change_max_invalid_logon_count";
    }

    public static class License {
        public static final String TYPE_CREATE = "create";
        public static final String TYPE_REMOVE = "remove";
    }
}
