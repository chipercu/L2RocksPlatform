package com.fuzzy.subsystem.core.scheduler.systemevents;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.emailmessages.systemevents.SystemEventsMessageSenderImpl;
import com.fuzzy.subsystem.core.remote.mail.RControllerHelpDeskGetterWrapper;
import com.fuzzy.subsystem.core.remote.mail.RControllerMailConfigGetterWrapper;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.scheduler.Job;
import com.fuzzy.subsystems.scheduler.RepeatableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

import static com.fuzzy.subsystem.core.CoreConfig.*;

public class MailSendSystemEventSchedulerQuery extends QuerySystem<Void> {
    private static final Logger log = LoggerFactory.getLogger(MailSendSystemEventSchedulerQuery.class);

    private final CoreSubsystem coreSubsystem;
    private final SendMailSystemEventsConfig config;
    private MailSendSystemEventJob mailSendSystemEventJob;

    public MailSendSystemEventSchedulerQuery(CoreSubsystem coreSubsystem) {
        this.coreSubsystem = coreSubsystem;
        config = coreSubsystem.getConfig().getMailSendSystemEventsConfig();
    }

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        mailSendSystemEventJob = MailSendSystemEventJob.newBuilder()
                .withCoreSubsystem(coreSubsystem)
                .withMailConfigGetter(resources.getQueryRemoteController(CoreSubsystem.class, RControllerMailConfigGetterWrapper.class))
                .withHelpDeskGetter(coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerHelpDeskGetterWrapper.class))
                .withSystemEventsMessageSender(new SystemEventsMessageSenderImpl(coreSubsystem))
                .build();
    }

    @Override
    public Void execute(ContextTransaction context) throws PlatformException {
        if (isMailSendSystemEventsConfigured()) {
            scheduleJob();
        }
        return null;
    }

    private void scheduleJob() throws PlatformException {
        scheduleQuartzJob(MailSendSystemEventTriggerBuilder.build(coreSubsystem),
                () -> mailSendSystemEventJob);
        writeLogMessage();
    }

    private String scheduleQuartzJob(RepeatableTrigger trigger, Supplier<Job> jobFactory) throws PlatformException {
        return coreSubsystem.getScheduler().scheduleJob(trigger, jobFactory);
    }


    private boolean isMailSendSystemEventsConfigured() throws PlatformException {
        final boolean configured = config.isConfigured();
        if (configured) {
            final Duration delay = config.getDelay();
            if (Objects.isNull(delay)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(JSON_SEND_MAIL_EVENT + "." + JSON_DELAY_SEND_MAIL_EVENT);
            }

            final Duration interval = config.getInterval();
            if (Objects.isNull(interval)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(JSON_SEND_MAIL_EVENT + "." + JSON_INTERVAL_SEND_MAIL_EVENT);
            }
        }
        return configured;
    }


    private void writeLogMessage() {
        log.info("CoreSubsystem started mail send system event background process, delay:{} interval:{}",
                config.getDelay(),
                config.getInterval());
    }
}