package com.fuzzy.subsystem.core.config;

import com.fuzzy.subsystems.config.*;

public class CoreConfigDescription {

    public static final EnumConfig<DisplayNameFormat> DISPLAY_NAME_FORMAT = new EnumConfig<>(
            "display_name_format", DisplayNameFormat.FIRST_SECOND, DisplayNameFormat.class);

    public static final EnumConfig<ServerStatus> SERVER_STATUS = new EnumConfig<>(
            "server_status", ServerStatus.NOT_INIT, ServerStatus.class);

    public static final EnumConfig<Language> SERVER_LANGUAGE = new EnumConfig<>(
            "server_language", Language.ENGLISH, Language.class);

    public static final EnumConfig<FirstDayOfWeek> FIRST_DAY_OF_WEEK = new EnumConfig<>(
            "first_day_of_week", FirstDayOfWeek.MONDAY, FirstDayOfWeek.class);

    public static class SecurityConfig {

        public static final BooleanConfig COMPLEX_PASSWORD = new BooleanConfig(
                "complex_password", false
        );

        public static final IntegerConfig MIN_PASSWORD_LENGTH = new IntegerConfig(
                "min_password_length", null
        );

        public static final LongConfig PASSWORD_EXPIRATION_TIME = new LongConfig(
                "password_expiration_time", null
        );

        public static final IntegerConfig MAX_INVALID_LOGON_COUNT = new IntegerConfig(
                "max_invalid_logon_count", null
        );
    }

    @Deprecated
    public static final ByteArrayConfig LICENSE = new ByteArrayConfig(
            "license", null
    );
}
