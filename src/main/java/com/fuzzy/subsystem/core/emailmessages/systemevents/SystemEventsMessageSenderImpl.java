package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.mail.Mail;
import com.fuzzy.subsystem.core.remote.mail.RControllerMailSenderWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemEventsMessageSenderImpl implements SystemEventsMessageSender {

    private static final Logger log = LoggerFactory.getLogger(SystemEventsMessageSenderImpl.class);

    private final RControllerMailSenderWrapper mailSenderWrapper;

    public SystemEventsMessageSenderImpl(CoreSubsystem coreSubsystem) {
        this.mailSenderWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerMailSenderWrapper.class);
    }

    @Override
    public void sendAsync(EmployeeReadable employee, SystemEventsMessage message, QueryTransaction transaction) throws PlatformException {
        if (StringUtils.isEmpty(employee.getEmail())) {
            return;
        }
        Mail mail = new Mail().withRecipients(employee.getEmail());
        mail.withTitle(message.getTitle())
                .withContentType(message.getContentType())
                .withBody(message.getBody())
                .appendEmbeddedImageAttachments(message.getEmbeddedImageAttachments())
                .appendFileAttachments(message.getFileAttachments());
        log.info("SystemEventsMessageSender started sending message to the email user with id:{}", employee.getId());
        mailSenderWrapper.sendMailAsync(mail);
    }
}