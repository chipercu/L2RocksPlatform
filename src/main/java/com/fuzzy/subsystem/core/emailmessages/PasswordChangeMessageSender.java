package com.fuzzy.subsystem.core.emailmessages;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenManager;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenManagerFactory;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenReadable;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import com.fuzzy.subsystem.core.remote.mail.Mail;
import com.fuzzy.subsystem.core.remote.mail.RControllerHelpDeskGetterWrapper;
import com.fuzzy.subsystem.core.remote.mail.RControllerMailSenderWrapper;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.remote.info.RControllerFrontendInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class PasswordChangeMessageSender {


    private final RControllerMailSenderWrapper mailSenderWrapper;
    private final RControllerHelpDeskGetterWrapper helpDeskGetterWrapper;
    private CoreSubsystem coreSubsystem;
    private RControllerFrontendInfo rControllerFrontendInfo;
    private LanguageGetter languageGetter;
    private EmployeeTokenManager employeeTokenManager;
    private EmployeeDisplayNameForMessageBuilder employeeDisplayNameBuilder;


    public PasswordChangeMessageSender(CoreSubsystem coreSubsystem, ResourceProvider resources) {
        this.coreSubsystem = coreSubsystem;
        rControllerFrontendInfo =
                resources.getQueryRemoteController(FrontendSubsystem.class, RControllerFrontendInfo.class);
        languageGetter = new LanguageGetter(resources);
        employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeTokenRestoreAccessManager(coreSubsystem, resources);
        employeeDisplayNameBuilder = new EmployeeDisplayNameForMessageBuilder(resources);
        mailSenderWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerMailSenderWrapper.class);
        helpDeskGetterWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerHelpDeskGetterWrapper.class);
    }

    public void sendAsync(EmployeeReadable employee, boolean changeByAdmin, QueryTransaction transaction)
            throws PlatformException {
        if (StringUtils.isEmpty(employee.getEmail())) {
            return;
        }
        Language language = languageGetter.get(employee, transaction);
        Mail mail = new Mail().withRecipients(employee.getEmail());
        String employeeDisplayName = employeeDisplayNameBuilder.build(employee, transaction);
        final HelpDeskConfig helpDeskConfig = helpDeskGetterWrapper.getConfig();
        if (Objects.nonNull(helpDeskConfig)) {
            if (changeByAdmin) {
                PasswordChangeByAdminMessage message =
                        PasswordChangeByAdminMessage.newInstance(coreSubsystem, language, helpDeskConfig);
                mail.withTitle(message.getTitle())
                        .withContentType(message.getContentType())
                        .withBody(message.getBody(employeeDisplayName))
                        .appendEmbeddedImageAttachments(message.getEmbeddedImageAttachments())
                        .appendFileAttachments(message.getFileAttachments());
            } else {
                PasswordChangeByEmployeeMessage message =
                        PasswordChangeByEmployeeMessage.newInstance(coreSubsystem, language, helpDeskConfig);
                String serverUrl = rControllerFrontendInfo.getServerUrl();
                EmployeeTokenReadable employeeToken = employeeTokenManager.createToken(employee.getId(), transaction);
                mail.withTitle(message.getTitle())
                        .withContentType(message.getContentType())
                        .withBody(message.getBody(employeeDisplayName, serverUrl, employeeToken.getToken()))
                        .appendEmbeddedImageAttachments(message.getEmbeddedImageAttachments())
                        .appendFileAttachments(message.getFileAttachments());
            }
            mailSenderWrapper.sendMailAsync(mail);
        }
    }
}
