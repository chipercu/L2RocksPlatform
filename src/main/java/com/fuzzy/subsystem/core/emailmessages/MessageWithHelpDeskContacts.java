package com.fuzzy.subsystem.core.emailmessages;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import com.fuzzy.subsystem.core.remote.mail.FileMailAttachment;
import com.fuzzy.subsystems.emailmessages.Message;
import com.fuzzy.subsystems.subsystem.Subsystem;

public class MessageWithHelpDeskContacts extends Message {

    private final HelpDeskConfig helpDeskConfig;

    protected MessageWithHelpDeskContacts(Subsystem subsystem,
                                          Language language,
                                          HelpDeskConfig helpDeskConfig,
                                          String titleLocalizationKey,
                                          String resourceDir,
                                          String bodyRes,
                                          String[] pngImagesRes,
                                          FileMailAttachment[] fileAttachments) throws PlatformException {
        super(subsystem, language, titleLocalizationKey, resourceDir, bodyRes, pngImagesRes, fileAttachments);
        this.helpDeskConfig = helpDeskConfig;
    }

    protected MessageWithHelpDeskContacts(Subsystem subsystem,
                                          Language language,
                                          HelpDeskConfig helpDeskConfig,
                                          String titleLocalizationKey,
                                          String resourceDir,
                                          String bodyRes,
                                          String[] pngImagesRes) throws PlatformException {
        super(subsystem, language, titleLocalizationKey, resourceDir, bodyRes, pngImagesRes);
        this.helpDeskConfig = helpDeskConfig;
    }

    @Override
    protected String getBody(String[] vars, String[] values) {
        String[] newVars = new String[vars.length + 2];
        System.arraycopy(vars, 0, newVars, 0, vars.length);
        newVars[vars.length] = "{{help_desk_email}}";
        newVars[vars.length + 1] = "{{help_desk_phone_number}}";
        String[] newValues = new String[values.length + 2];
        System.arraycopy(values, 0, newValues, 0, vars.length);
        newValues[vars.length] = helpDeskConfig.getEmail();
        newValues[vars.length + 1] = helpDeskConfig.getPhoneNumber();
        return super.getBody(newVars, newValues);
    }
}
