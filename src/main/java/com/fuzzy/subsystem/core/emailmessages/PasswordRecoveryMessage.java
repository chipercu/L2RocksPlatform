package com.fuzzy.subsystem.core.emailmessages;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import org.apache.commons.lang3.StringUtils;

public class PasswordRecoveryMessage extends MessageWithHelpDeskContacts {

    private PasswordRecoveryMessage(CoreSubsystem coreSubsystem,
                                    Language language,
                                    HelpDeskConfig helpDeskConfig) throws PlatformException {
        super(
                coreSubsystem,
                language,
                helpDeskConfig,
                CoreSubsystemConsts.Localization.Mail.PASSWORD_RECOVERY_MESSAGE_TITLE,
                "mail/password_recovery/",
                "password_recovery.html",
                new String[] {"background.png", "email.png", "logo.png", "phone.png"}
        );
    }

    public String getBody(String serverUrl, String passwordRecoveryToken) {
        String passwordRecoveryLink = StringUtils.appendIfMissing(serverUrl, "/") +
                "im/change-password/" + passwordRecoveryToken;
        String[] vars = {"{{password_recovery_link}}"};
        String[] values = {passwordRecoveryLink};
        return getBody(vars, values);
    }

    public static PasswordRecoveryMessage newInstance(CoreSubsystem coreSubsystem,
                                                      Language language,
                                                      HelpDeskConfig helpDeskConfig) throws PlatformException {
        return new PasswordRecoveryMessage(coreSubsystem, language, helpDeskConfig);
    }
}
