package com.fuzzy.subsystem.core;

import com.fuzzy.main.cluster.struct.Info;
import com.infomaximum.main.SubsystemsConfig;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.subsystem.SubsystemConfig;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

public class CoreConfig extends SubsystemConfig {

    public static final Duration DEFAULT_TIMEOUT_RESTORE_LINK = Duration.ofDays(1);
    public static final Duration DEFAULT_DURATION_RESET_COUNT_INVALID_LOGON = Duration.ofMinutes(10);
    public static final LogonType DEFAULT_LOGON_TYPE = LogonType.LOGIN;

    private static final String DEFAULT_SECRET_KEY_PATH = "secret_key/secret_key";
    private static final String JSON_TIMEOUT_RESTORE_LINK = "restorelink_timeout";
    private static final String JSON_DURATION_RESET_COUNT_INVALID_LOGON = "reset_count_invalid_logon_duration";
    private static final String JSON_LOGON_TYPE = "logon_type";
    private static final String JSON_SECRET_KEY_PATH = "secret_key_path";
    private static final String JSON_SYSTEM_NOTIFICATION_MESSAGE = "system_notification_message";
    public static final String JSON_EMPLOYEE_TREE_DEPTH = "employee_tree_depth";
    public static final String JSON_SEND_MAIL_EVENT = "send_mail_system_events";
    public static final String JSON_DELAY_SEND_MAIL_EVENT = "delay";
    public static final String JSON_INTERVAL_SEND_MAIL_EVENT = "interval";
    public static final String JSON_DEBUG_MODE = "debug";

    public static final Integer DEFAULT_EMPLOYEE_TREE_DEPTH = 20;
    public static final Duration DEFAULT_DELAY_SEND_MAIL_EVENT = Duration.ofSeconds(60);
    public static final Duration DEFAULT_INTERVAL_SEND_MAIL_EVENT = Duration.ofMinutes(60);
    public static final boolean DEFAULT_DEBUG_MODE = false;

    private final Duration timeoutRestoreLink;
    private final Duration durationResetCountInvalidLogon;
    private final LogonType logonType;
    private final Path secretKeyPath;
    private final String systemNotificationMessage;
    private final Integer systemNotificationMessageHash;
    private final Integer employeeTreeDepth;
    private final SendMailSystemEventsConfig sendMailSystemEventsConfig;
    private final boolean debugMode;

    private CoreConfig(Builder builder) {
        super(builder);

        this.timeoutRestoreLink = builder.timeoutRestoreLink;
        this.durationResetCountInvalidLogon = builder.durationResetCountInvalidLogon;
        this.logonType = builder.logonType;
        this.secretKeyPath = builder.getSecretKeyPath();
        this.systemNotificationMessage = builder.systemNotificationMessage;
        systemNotificationMessageHash = Objects.hashCode(RegExUtils.removeAll(systemNotificationMessage, "[^А-Яа-яA-Za-z0-9]")
                .toLowerCase(Locale.ROOT));
        this.employeeTreeDepth = builder.employeeTreeDepth;
        this.sendMailSystemEventsConfig = new SendMailSystemEventsConfig(
                builder.delaySendMailEvent,
                builder.intervalSendMailEvent,
                builder.sendMailEventConfig
        );
        this.debugMode = builder.debugMode;

    }

    public Duration getTimeoutRestoreLink() {
        return timeoutRestoreLink;
    }

    public Duration getDurationResetCountInvalidLogon() {
        return durationResetCountInvalidLogon;
    }

    public LogonType getLogonType() {
        return logonType;
    }

    public Path getSecretKeyPath() {
        return secretKeyPath;
    }

    public SystemNotification getSystemNotificationMessage() {
        return new SystemNotification(systemNotificationMessage, systemNotificationMessageHash);
    }

    public Integer getEmployeeTreeDepth() {
        return employeeTreeDepth;
    }

    public SendMailSystemEventsConfig getMailSendSystemEventsConfig() {
        return sendMailSystemEventsConfig;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public static class Builder extends SubsystemConfig.Builder {

        private Duration timeoutRestoreLink;
        private Duration durationResetCountInvalidLogon;
        private LogonType logonType;
        private String secretKeyPath;
        private String systemNotificationMessage;
        private Integer employeeTreeDepth;
        private Duration delaySendMailEvent;
        private Duration intervalSendMailEvent;
        private boolean sendMailEventConfig;
        private boolean debugMode;

        public Builder(Info subSystemInfo, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            super(subSystemInfo, subsystemsConfig);
            init();
        }

        public Builder withLogonType(LogonType value) {
            this.logonType = value;
            return this;
        }

        public Path getSecretKeyPath() {
            Path secretKeyPath = Paths.get(this.secretKeyPath);
            if (!secretKeyPath.isAbsolute()) {
                secretKeyPath = getSubsystemsConfig().getDataDir().resolve(secretKeyPath).toAbsolutePath();
            }
            return secretKeyPath;
        }

        @Override
        public CoreConfig build() {
            return new CoreConfig(this);
        }

        private void init() throws ConfigBuilderException {
            JSONObject json = readJSON();
            if (json.isEmpty()) {
                initDefault();
                save();
            } else {
                loadFrom(json);
            }
        }

        private void initDefault() {
            this.timeoutRestoreLink = DEFAULT_TIMEOUT_RESTORE_LINK;
            this.durationResetCountInvalidLogon = DEFAULT_DURATION_RESET_COUNT_INVALID_LOGON;
            this.logonType = DEFAULT_LOGON_TYPE;
            this.secretKeyPath = DEFAULT_SECRET_KEY_PATH;
            this.systemNotificationMessage = StringUtils.EMPTY;
            this.employeeTreeDepth = DEFAULT_EMPLOYEE_TREE_DEPTH;
            this.delaySendMailEvent = DEFAULT_DELAY_SEND_MAIL_EVENT;
            this.intervalSendMailEvent = DEFAULT_INTERVAL_SEND_MAIL_EVENT;
            this.sendMailEventConfig = true;
            this.debugMode = DEFAULT_DEBUG_MODE;
        }

        private void loadFrom(JSONObject json) {
            if (json.containsKey(JSON_TIMEOUT_RESTORE_LINK)) {
                timeoutRestoreLink = parseDuration(json.getAsString(JSON_TIMEOUT_RESTORE_LINK));
            } else {
                timeoutRestoreLink = DEFAULT_TIMEOUT_RESTORE_LINK;
            }
            if (json.containsKey(JSON_DURATION_RESET_COUNT_INVALID_LOGON)) {
                durationResetCountInvalidLogon = parseDuration(json.getAsString(JSON_DURATION_RESET_COUNT_INVALID_LOGON));
            } else {
                durationResetCountInvalidLogon = DEFAULT_DURATION_RESET_COUNT_INVALID_LOGON;
            }
            if (json.containsKey(JSON_LOGON_TYPE)) {
                logonType = LogonType.get(json.getAsString(JSON_LOGON_TYPE));
                if (logonType == null) {
                    throw new ConfigBuilderException("Invalid logon type");
                }
            } else {
                logonType = DEFAULT_LOGON_TYPE;
            }
            if (json.containsKey(JSON_SECRET_KEY_PATH)) {
                secretKeyPath = json.getAsString(JSON_SECRET_KEY_PATH);
            } else {
                secretKeyPath = DEFAULT_SECRET_KEY_PATH;
            }
            if (json.containsKey(JSON_SYSTEM_NOTIFICATION_MESSAGE)) {
                systemNotificationMessage = json.getAsString(JSON_SYSTEM_NOTIFICATION_MESSAGE);
            } else {
                systemNotificationMessage = StringUtils.EMPTY;
            }
            if (json.containsKey(JSON_EMPLOYEE_TREE_DEPTH)) {
                employeeTreeDepth = json.getAsNumber(JSON_EMPLOYEE_TREE_DEPTH).intValue();
                checkEmployeeTreeDepthRange(employeeTreeDepth);
            } else {
                employeeTreeDepth = DEFAULT_EMPLOYEE_TREE_DEPTH;
            }
            loadMailEventSendConfig(json);
            loadDebugModeConfig(json);
        }

        private void loadDebugModeConfig(JSONObject json) {
            if (json.containsKey(JSON_DEBUG_MODE)) {
                debugMode = (boolean) json.get(JSON_DEBUG_MODE);
            } else {
                debugMode = DEFAULT_DEBUG_MODE;
            }
        }

        private void loadMailEventSendConfig(JSONObject json) {
            if (json.containsKey(JSON_SEND_MAIL_EVENT)) {
                sendMailEventConfig = Boolean.TRUE;
                final JSONObject synchronizationConfig = (JSONObject) json.get(JSON_SEND_MAIL_EVENT);
                if (synchronizationConfig.containsKey(JSON_DELAY_SEND_MAIL_EVENT)) {
                    delaySendMailEvent = parseDuration(synchronizationConfig.getAsString(JSON_DELAY_SEND_MAIL_EVENT));
                }
                if (synchronizationConfig.containsKey(JSON_INTERVAL_SEND_MAIL_EVENT)) {
                    intervalSendMailEvent = parseDuration(synchronizationConfig.getAsString(JSON_INTERVAL_SEND_MAIL_EVENT));
                }
            }
        }

        private void checkEmployeeTreeDepthRange(Integer employeeTreeDepth) {
            if (employeeTreeDepth < 1 || employeeTreeDepth > 100) {
                throw new ConfigBuilderException(String.format("Config %s [%d] has out of range 1..100", JSON_EMPLOYEE_TREE_DEPTH, employeeTreeDepth));
            }
        }

        public void save() {
            JSONObject json = new JSONObject();
            json.put(JSON_TIMEOUT_RESTORE_LINK, packDuration(timeoutRestoreLink));
            json.put(JSON_DURATION_RESET_COUNT_INVALID_LOGON, packDuration(durationResetCountInvalidLogon));
            json.put(JSON_LOGON_TYPE, logonType.name().toLowerCase());
            json.put(JSON_SECRET_KEY_PATH, secretKeyPath);
            json.put(JSON_EMPLOYEE_TREE_DEPTH, employeeTreeDepth);
            json.appendField(JSON_SEND_MAIL_EVENT,
                    new JSONObject()
                            .appendField(JSON_DELAY_SEND_MAIL_EVENT, packDuration(DEFAULT_DELAY_SEND_MAIL_EVENT))
                            .appendField(JSON_INTERVAL_SEND_MAIL_EVENT, packDuration(DEFAULT_INTERVAL_SEND_MAIL_EVENT)));
            json.appendField(JSON_DEBUG_MODE, DEFAULT_DEBUG_MODE);
            saveJSON(json);
        }
    }

    public static class SystemNotification {
        private final String message;
        private final Integer hash;

        public SystemNotification(String message, Integer hash) {
            this.message = message;
            this.hash = hash;
        }

        public String getMessage() {
            return message;
        }

        public Integer getHash() {
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SystemNotification)) return false;
            SystemNotification that = (SystemNotification) o;
            return message.equals(that.message) && hash.equals(that.hash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, hash);
        }
    }

    public static class SendMailSystemEventsConfig {
        private final Duration delay;
        private final Duration interval;
        private final boolean isConfig;

        public SendMailSystemEventsConfig(Duration delay, Duration interval, boolean isConfig) {
            this.delay = delay;
            this.interval = interval;
            this.isConfig = isConfig;
        }

        public Duration getDelay() {
            return delay;
        }

        public Duration getInterval() {
            return interval;
        }

        public boolean isConfigured() {
            return isConfig;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendMailSystemEventsConfig that = (SendMailSystemEventsConfig) o;

            if (isConfig != that.isConfig) return false;
            if (!Objects.equals(delay, that.delay)) return false;
            return Objects.equals(interval, that.interval);
        }

        @Override
        public int hashCode() {
            int result = delay != null ? delay.hashCode() : 0;
            result = 31 * result + (interval != null ? interval.hashCode() : 0);
            result = 31 * result + (isConfig ? 1 : 0);
            return result;
        }
    }
}