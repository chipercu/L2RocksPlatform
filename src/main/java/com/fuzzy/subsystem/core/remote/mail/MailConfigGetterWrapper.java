package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.platform.exception.PlatformException;

public interface MailConfigGetterWrapper {
    Boolean isMailConfigured() throws PlatformException;
}