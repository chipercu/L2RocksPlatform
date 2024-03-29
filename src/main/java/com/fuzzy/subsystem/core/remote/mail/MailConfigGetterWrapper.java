package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.platform.exception.PlatformException;

public interface MailConfigGetterWrapper {
    Boolean isMailConfigured() throws PlatformException;
}