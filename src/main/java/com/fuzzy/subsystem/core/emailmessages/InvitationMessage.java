package com.fuzzy.subsystem.core.emailmessages;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import org.apache.commons.lang3.StringUtils;

public class InvitationMessage extends MessageWithHelpDeskContacts {

    private CoreSubsystem coreSubsystem;
    private Language language;

    private InvitationMessage(CoreSubsystem coreSubsystem,
                              Language language,
                              HelpDeskConfig helpDeskConfig) throws PlatformException {
        super(
                coreSubsystem,
                language,
                helpDeskConfig,
                CoreSubsystemConsts.Localization.Mail.INVITATION_MESSAGE_TITLE,
                "mail/invitation/",
                "invitation.html",
                new String[] {"background.png", "email.png", "logo.png", "phone.png"}
        );
        this.coreSubsystem = coreSubsystem;
        this.language = language;
    }

    public String getBody(String employeeLogin, String employeeDisplayName, String serverUrl, String invitationToken, String invitationTimeout) {
        if (StringUtils.isEmpty(employeeLogin)) {
            employeeLogin = coreSubsystem.getMessageSource().getString(
                    CoreSubsystemConsts.Localization.Mail.EMPTY_LOGIN, language);
        }
        String invitationLink = StringUtils.appendIfMissing(serverUrl, "/") +
                "im/invite-set-password/" + invitationToken;
        String[] vars = {"{{employee_login}}","{{employee_display_name}}", "{{invitation_link}}", "{{invitation_timeout}}"};
        String[] values = {employeeLogin, employeeDisplayName, invitationLink, invitationTimeout};
        return getBody(vars, values);
    }

    public static InvitationMessage newInstance(CoreSubsystem coreSubsystem,
                                                Language language,
                                                HelpDeskConfig helpDeskConfig) throws PlatformException {
        return new InvitationMessage(coreSubsystem, language, helpDeskConfig);
    }
}
