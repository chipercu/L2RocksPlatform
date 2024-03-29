package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;

import java.time.Duration;

public interface RControllerMailSenderWrapper extends RController {

    void sendMail(Mail mail) throws PlatformException;

    void sendMailAsync(Mail mail) throws PlatformException;

    void sendMail(Mail mail, Duration timeout) throws PlatformException;

    void sendMailAsync(Mail mail, Duration timeout) throws PlatformException;
}