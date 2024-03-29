package com.fuzzy.subsystem.core.config;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.config.CoreConfigReadable;
import com.fuzzy.subsystems.config.ConfigGetter;

import java.time.Duration;

public class CoreConfigGetter extends ConfigGetter<CoreConfigReadable> {

    public CoreConfigGetter(ResourceProvider resources) {
        super(resources.getReadableResource(CoreConfigReadable.class));
    }

    public AuthenticationConfig getAuthenticationConfig(QueryTransaction transaction) throws PlatformException {
        return new AuthenticationConfig(
                getComplexPassword(transaction),
                getPasswordExpirationTime(transaction),
                get(CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT, transaction)
        );
    }

    private ComplexPassword getComplexPassword(QueryTransaction transaction) throws PlatformException {
        boolean isComplexPassword = get(CoreConfigDescription.SecurityConfig.COMPLEX_PASSWORD, transaction);
        if (!isComplexPassword) {
            return null;
        }
        return new ComplexPassword(get(CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH, transaction));
    }

    private Duration getPasswordExpirationTime(QueryTransaction transaction)
            throws PlatformException {
        Long passwordExpirationTimeSeconds =
                get(CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME, transaction);
        return passwordExpirationTimeSeconds != null ? Duration.ofSeconds(passwordExpirationTimeSeconds) : null;
    }
}
