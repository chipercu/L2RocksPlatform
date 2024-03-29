package com.fuzzy.subsystem.core.scheduler.systemevents;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.emailmessages.systemevents.SystemEventsMessage;
import com.fuzzy.subsystem.core.emailmessages.systemevents.SystemEventsMessageSender;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import com.fuzzy.subsystem.core.remote.mail.HelpDeskGetterWrapper;
import com.fuzzy.subsystem.core.remote.mail.MailConfigGetterWrapper;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.scheduler.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static com.fuzzy.subsystem.core.service.systemevent.SystemEvent.EventLevel;

public class MailSendSystemEventJob extends Job {

    private static final Logger log = LoggerFactory.getLogger(MailSendSystemEventJob.class);

    private final SystemEventService systemEventService;
    private MailConfigGetterWrapper mailConfigGetterWrapper;
    private HelpDeskGetterWrapper helpDeskGetterWrapper;
    private SystemEventsMessageSender systemEventsMessageSender;
    private final CoreSubsystem coreSubsystem;
    private MailRecipientGetter mailRecipientGetter;
    private LanguageGetter languageGetter;


    private MailSendSystemEventJob(Builder builder) {
        this.coreSubsystem = builder.coreSubsystem;
        this.systemEventsMessageSender = builder.systemEventsMessageSender;
        this.systemEventService = builder.coreSubsystem.getSystemEventService();
        this.mailConfigGetterWrapper = builder.mailConfigGetter;
        this.helpDeskGetterWrapper = builder.helpDeskGetter;
    }

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        mailRecipientGetter = new MailRecipientGetter(resources);
        languageGetter = new LanguageGetter(resources);

    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        if (!mailConfigGetterWrapper.isMailConfigured()) {
            log.error("MailSendSystemEventJob not started because, not mail server configuration");
            return null;
        }

        final HelpDeskConfig helpDeskConfig = helpDeskGetterWrapper.getConfig();
        if (Objects.nonNull(helpDeskConfig)) {
            final ArrayList<SystemEvent> events = systemEventService.getActualSortedEvents(ZonedDateTime.now(), Set.of(EventLevel.CRITICAL, EventLevel.ERROR));
            if (events.isEmpty()) {
                return null;
            }

            for (EmployeeReadable recipient : mailRecipientGetter.getRecipients(transaction)) {
                final SystemEventsMessage systemEventsMessage = SystemEventsMessage.newInstance(coreSubsystem,
                        events,
                        languageGetter.get(recipient, transaction),
                        helpDeskConfig);
                systemEventsMessageSender.sendAsync(recipient, systemEventsMessage, transaction);
            }
        }
        return null;
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private MailConfigGetterWrapper mailConfigGetter;
        private HelpDeskGetterWrapper helpDeskGetter;
        private SystemEventsMessageSender systemEventsMessageSender;
        private CoreSubsystem coreSubsystem;

        private Builder() {
        }


        public Builder withMailConfigGetter(MailConfigGetterWrapper mailConfigGetter) {
            this.mailConfigGetter = mailConfigGetter;
            return this;
        }

        public Builder withHelpDeskGetter(HelpDeskGetterWrapper helpDeskGetter) {
            this.helpDeskGetter = helpDeskGetter;
            return this;
        }

        public Builder withSystemEventsMessageSender(SystemEventsMessageSender systemEventsMessageSender) {
            this.systemEventsMessageSender = systemEventsMessageSender;
            return this;
        }

        public Builder withCoreSubsystem(CoreSubsystem coreSubsystem) {
            this.coreSubsystem = coreSubsystem;
            return this;
        }

        public MailSendSystemEventJob build() {
            return new MailSendSystemEventJob(this);
        }
    }
}