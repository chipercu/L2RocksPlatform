package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.emailmessages.MessageWithHelpDeskContacts;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;

import java.util.ArrayList;

public class SystemEventsMessage extends MessageWithHelpDeskContacts {

    private final SystemEventsBodyHandler systemEventsBodyHandler;

    private SystemEventsMessage(CoreSubsystem coreSubsystem,
                                ArrayList<SystemEvent> events,
                                Language language,
                                HelpDeskConfig helpDeskConfig) throws PlatformException {
        super(
                coreSubsystem,
                language,
                helpDeskConfig,
                CoreSubsystemConsts.Localization.Mail.SYSTEM_EVENTS_TITLE,
                "mail/system_events/",
                "system_events.html",
                new String[]{ "background.png", "email.png", "logo.png", "phone.png" }
        );
        systemEventsBodyHandler = new SystemEventsBodyHandler(events);
    }

    public String getBody() {
        bodyTemplate = systemEventsBodyHandler.fillBody(bodyTemplate);
        return getBody(new String[0], new String[0]);
    }

    public static SystemEventsMessage newInstance(CoreSubsystem coreSubsystem,
                                                  ArrayList<SystemEvent> events,
                                                  Language language,
                                                  HelpDeskConfig helpDeskConfig) throws PlatformException {
        return new SystemEventsMessage(coreSubsystem, events, language, helpDeskConfig);
    }
}