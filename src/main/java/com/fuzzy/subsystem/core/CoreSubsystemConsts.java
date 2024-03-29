package com.fuzzy.subsystem.core;

public class CoreSubsystemConsts {

    public static final String UUID = "com.infomaximum.subsystem.core";

    public static class Localization {

        public static final String COPY = "copy";

        public static class Mail {
            public static final String PASSWORD_RECOVERY_MESSAGE_TITLE = "mail.password_recovery.message_title";
            public static final String DISABLED_LOGON_MESSAGE_TITLE= "mail.logon_disabled.message_title";
            public static final String PASSWORD_CHANGE_BY_ADMIN_MESSAGE_TITLE = "mail.password_change_by_admin.message_title";
            public static final String PASSWORD_CHANGE_BY_EMPLOYEE_MESSAGE_TITLE = "mail.password_change_by_employee.message_title";
            public static final String INVITATION_MESSAGE_TITLE = "mail.invitation.message_title";
            public static final String SYSTEM_EVENTS_TITLE = "mail.system_events_title";
            public static final String EMPTY_LOGIN = "mail.empty_login";
        }

        public static class AccessRole {
            public static final String ADMINISTRATOR = "access_role.administrator";
            public static final String SECURITY_ADMINISTRATOR = "access_role.security_administrator";
        }

        public static class AuthenticationType {
            public static final String INTEGRATED = "authentication_type.integrated";
        }

        public static class AuthenticationName {
            public static final String INTEGRATED = "authentication_name.integrated";
        }
    }

    public static class EmployeeSystemFields {
        public static final String FIRST_NAME_KEY = build("first_name");
        public static final String PATRONYMIC_KEY = build("patronymic");
        public static final String SECOND_NAME_KEY = build("second_name");
        public static final String LANGUAGE_KEY = build("language");
        public static final String DEPARTMENT_ID_KEY = build("department_id");
        public static final String PERSONNEL_NUMBER_KEY = build("personnel_number");
        public static final String EMAIL_KEY = build("email");
        public static final String PHONE_NUMBER_KEY = build("phone_number");

        private static String build(String key) {
            return UUID + '.' + key;
        }
    }

    public static class AuthenticationTypes {
        public static final String INTEGRATED = build("integrated");

        private static String build(String key) {
            return UUID + '.' + key;
        }
    }

    public static class Mail {
        public static final int INVITATION_TOKEN_EXPIRATION_DAY = 14;
    }
}
