package com.fuzzy.subsystem.core.emailmessages;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import org.apache.commons.lang3.StringUtils;

public class PasswordChangeByEmployeeMessage extends MessageWithHelpDeskContacts {

    private PasswordChangeByEmployeeMessage(CoreSubsystem coreSubsystem,
                                            Language language,
                                            HelpDeskConfig helpDeskConfig) throws PlatformException {
        super(
                coreSubsystem,
                language,
                helpDeskConfig,
                CoreSubsystemConsts.Localization.Mail.PASSWORD_CHANGE_BY_EMPLOYEE_MESSAGE_TITLE,
                "mail/password_change_by_employee/",
                "password_change_by_employee.html",
                new String[] {"background.png", "email.png", "logo.png", "phone.png"}
        );
    }

    public String getBody(String employeeDisplayName, String serverUrl, String changePasswordToken) {
        String changePasswordLink = StringUtils.appendIfMissing(serverUrl, "/") +
                "im/change-password/" + changePasswordToken;
        String[] vars = {"{{employee_display_name}}", "{{change_password_link}}"};
        String[] values = { employeeDisplayName, changePasswordLink };
        return getBody(vars, values);
    }

    public static PasswordChangeByEmployeeMessage newInstance(CoreSubsystem coreSubsystem,
                                                              Language language,
                                                              HelpDeskConfig helpDeskConfig) throws PlatformException {
        return new PasswordChangeByEmployeeMessage(coreSubsystem, language, helpDeskConfig);
    }
}
