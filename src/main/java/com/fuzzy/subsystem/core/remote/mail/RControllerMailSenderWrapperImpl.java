package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.cluster.core.remote.AbstractRController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

public class RControllerMailSenderWrapperImpl extends AbstractRController<CoreSubsystem> implements RControllerMailSenderWrapper {

    private static final Logger log = LoggerFactory.getLogger(RControllerMailSenderWrapperImpl.class);
    private final CoreSubsystem coreSubsystem;


    public RControllerMailSenderWrapperImpl(CoreSubsystem coreSubsystem) {
        super(coreSubsystem);
        this.coreSubsystem = coreSubsystem;
    }

    @Override
    public void sendMail(Mail mail) throws PlatformException {
        final RControllerMailSender rcControllerMailSender = getControllerMailSender(coreSubsystem);
        if (Objects.nonNull(rcControllerMailSender)) {
            rcControllerMailSender.sendMail(mail);
        }
    }

    @Override
    public void sendMailAsync(Mail mail) throws PlatformException {
        final RControllerMailSender rcControllerMailSender = getControllerMailSender(coreSubsystem);
        if (Objects.nonNull(rcControllerMailSender)) {
            rcControllerMailSender.sendMailAsync(mail);
        }
    }

    @Override
    public void sendMail(Mail mail, Duration timeout) throws PlatformException {
        final RControllerMailSender rcControllerMailSender = getControllerMailSender(coreSubsystem);
        if (Objects.nonNull(rcControllerMailSender)) {
            rcControllerMailSender.sendMail(mail, timeout);
        }
    }

    @Override
    public void sendMailAsync(Mail mail, Duration timeout) throws PlatformException {
        final RControllerMailSender rcControllerMailSender = getControllerMailSender(coreSubsystem);
        if (Objects.nonNull(rcControllerMailSender)) {
            rcControllerMailSender.sendMailAsync(mail, timeout);
        }
    }

    private RControllerMailSender getControllerMailSender(CoreSubsystem coreSubsystem) {
        final RControllerMailSender rcControllerMailSender;
        rcControllerMailSender = coreSubsystem.getRemotes().getControllers(RControllerMailSender.class)
                .stream()
                .findFirst()
                .orElse(null);
        if (Objects.isNull(rcControllerMailSender)) {
            log.error("RControllerMailSender implementation controller not found.");
        }
        return rcControllerMailSender;
    }
}