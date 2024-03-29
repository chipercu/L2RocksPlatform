package com.fuzzy.subsystem.core.emailmessages;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;

public class PasswordChangeByAdminMessage extends MessageWithHelpDeskContacts {

    private PasswordChangeByAdminMessage(CoreSubsystem coreSubsystem,
                                         Language language,
                                         HelpDeskConfig helpDeskConfig) throws PlatformException {
        super(
                coreSubsystem,
                language,
                helpDeskConfig,
                CoreSubsystemConsts.Localization.Mail.PASSWORD_CHANGE_BY_ADMIN_MESSAGE_TITLE,
                "mail/password_change_by_admin/",
                "password_change_by_admin.html",
                new String[] {"background.png", "email.png", "logo.png", "phone.png"}
        );
    }

    public String getBody(String employeeDisplayName) {
        String[] vars = {"{{employee_display_name}}"};
        String[] values = { employeeDisplayName };
        return getBody(vars, values);
    }

    public static PasswordChangeByAdminMessage newInstance(CoreSubsystem coreSubsystem,
                                                           Language language,
                                                           HelpDeskConfig helpDeskConfig) throws PlatformException {
        return new PasswordChangeByAdminMessage(coreSubsystem, language, helpDeskConfig);
    }
}
