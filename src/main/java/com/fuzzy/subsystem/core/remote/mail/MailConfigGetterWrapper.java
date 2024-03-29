package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.platform.exception.PlatformException;

public interface MailConfigGetterWrapper {
    Boolean isMailConfigured() throws PlatformException;
}