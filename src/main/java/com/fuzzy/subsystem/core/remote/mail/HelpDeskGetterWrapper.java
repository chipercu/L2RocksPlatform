package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;

public interface HelpDeskGetterWrapper {
    HelpDeskConfig getConfig() throws PlatformException;
}