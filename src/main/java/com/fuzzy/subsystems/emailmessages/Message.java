package com.fuzzy.subsystems.emailmessages;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.remote.mail.EmbeddedImageAttachment;
import com.fuzzy.subsystem.core.remote.mail.FileMailAttachment;
import com.fuzzy.subsystem.core.remote.mail.Mail;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class Message {

    private String title;
    protected String bodyTemplate;
    private EmbeddedImageAttachment[] embeddedImageAttachments;
    private FileMailAttachment[] fileAttachments;

    protected Message(
            Subsystem subsystem,
            Language language,
            String titleLocalizationKey,
            String resourceDir,
            String bodyRes,
            String[] pngImagesRes,
            FileMailAttachment[] fileAttachments
    ) throws PlatformException {
        title = subsystem.getMessageSource().getString(titleLocalizationKey, language);
        String resourcePath = resourceDir + language.getLocale().getLanguage() + "/";
        try {
            bodyTemplate = Resources.toString(
                    Resources.getResource(resourcePath + bodyRes), Charsets.UTF_8
            );
            embeddedImageAttachments = new EmbeddedImageAttachment[pngImagesRes.length];
            for (int i = 0; i < pngImagesRes.length; i++) {
                embeddedImageAttachments[i] = new EmbeddedImageAttachment(
                        pngImagesRes[i],
                        "image/png",
                        Resources.toByteArray(Resources.getResource(resourcePath + pngImagesRes[i])));
            }
            this.fileAttachments = fileAttachments;
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
    }

    protected Message(
            Subsystem subsystem,
            Language language,
            String titleLocalizationKey,
            String resourceDir,
            String bodyRes,
            String[] pngImagesRes
    ) throws PlatformException {
        this(subsystem, language, titleLocalizationKey, resourceDir, bodyRes, pngImagesRes, null);
    }

    public String getTitle() {
        return title;
    }

    public EmbeddedImageAttachment[] getEmbeddedImageAttachments() {
        return embeddedImageAttachments;
    }

    public FileMailAttachment[] getFileAttachments() {
        return fileAttachments;
    }

    public Mail.ContentType getContentType() {
        return Mail.ContentType.HTML_UTF8;
    }

    protected String getBody(String[] vars, String[] values) {
        return StringUtils.replaceEach(bodyTemplate, vars, values);
    }
}
