package com.fuzzy.subsystem.core.config;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.config.CoreConfigEditable;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.config.ConfigSetter;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

public class CoreConfigSetter extends ConfigSetter<CoreConfigEditable> {

    public CoreConfigSetter(ResourceProvider resources) {
        super(CoreConfigEditable.class, resources);
    }

    public void setComplexPassword(AuthenticationReadable authentication, boolean value, ContextTransaction<?> context)
            throws PlatformException {
        String cnfName = CoreConfigDescription.SecurityConfig.COMPLEX_PASSWORD.getName();
        CoreConfigEditable configEditable = configEditableResource.find(new HashFilter(CoreConfigEditable.FIELD_NAME, cnfName), context.getTransaction());
        Boolean oldValue = configEditable == null ? CoreConfigDescription.SecurityConfig.COMPLEX_PASSWORD.getDefaultValue() : TypeConvert.unpackBoolean(configEditable.getValue());

        set(CoreConfigDescription.SecurityConfig.COMPLEX_PASSWORD, value, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_CHANGE_COMPLEX_PASSWORD)
                        .withParam(CoreParameter.Authentication.OLD_VALUE, String.valueOf(oldValue))
                        .withParam(CoreParameter.Authentication.NEW_VALUE, String.valueOf(value)),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId())
                        .withParam(CoreParameter.Authentication.NAME, authentication.getName())
                        .withParam(CoreParameter.Authentication.TYPE, authentication.getType()),
                context
        );
    }

    public void setMinPasswordLength(AuthenticationReadable authentication, Integer value, ContextTransaction<?> context)
            throws PlatformException {
        String cnfName = CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH.getName();
        CoreConfigEditable configEditable = configEditableResource.find(new HashFilter(CoreConfigEditable.FIELD_NAME, cnfName), context.getTransaction());
        Integer oldValue = configEditable == null ? CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH.getDefaultValue() : TypeConvert.unpackInteger(configEditable.getValue());

        set(CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH, value, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_CHANGE_MIN_PASSWORD_LENGTH)
                        .withParam(CoreParameter.Authentication.OLD_VALUE, String.valueOf(oldValue))
                        .withParam(CoreParameter.Authentication.NEW_VALUE, String.valueOf(value)),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId())
                        .withParam(CoreParameter.Authentication.NAME, authentication.getName())
                        .withParam(CoreParameter.Authentication.TYPE, authentication.getType()),
                context
        );
    }

    public void setPasswordExpirationTime(AuthenticationReadable authentication, Long value, ContextTransaction<?> context)
            throws PlatformException {
        String cnfName = CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME.getName();
        CoreConfigEditable configEditable = configEditableResource.find(new HashFilter(CoreConfigEditable.FIELD_NAME, cnfName), context.getTransaction());
        Long oldValue = configEditable == null ? CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME.getDefaultValue() : TypeConvert.unpackLong(configEditable.getValue());

        set(CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME, value, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_CHANGE_PASSWORD_EXPIRATION_TIME)
                        .withParam(CoreParameter.Authentication.OLD_VALUE, String.valueOf(oldValue))
                        .withParam(CoreParameter.Authentication.NEW_VALUE, String.valueOf(value)),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId())
                        .withParam(CoreParameter.Authentication.NAME, authentication.getName())
                        .withParam(CoreParameter.Authentication.TYPE, authentication.getType()),
                context
        );
    }

    public void setMaxInvalidLogonCount(AuthenticationReadable authentication, Integer value, ContextTransaction<?> context)
            throws PlatformException {
        String cnfName = CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT.getName();
        CoreConfigEditable configEditable = configEditableResource.find(new HashFilter(CoreConfigEditable.FIELD_NAME, cnfName), context.getTransaction());
        Integer oldValue = configEditable == null ? CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT.getDefaultValue() : TypeConvert.unpackInteger(configEditable.getValue());

        set(CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT, value, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_CHANGE_MAX_INVALID_LOGON_COUNT)
                        .withParam(CoreParameter.Authentication.OLD_VALUE, String.valueOf(oldValue))
                        .withParam(CoreParameter.Authentication.NEW_VALUE, String.valueOf(value)),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId())
                        .withParam(CoreParameter.Authentication.NAME, authentication.getName())
                        .withParam(CoreParameter.Authentication.TYPE, authentication.getType()),
                context
        );
    }
}
