package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Mail implements RemoteObject {

    public enum ContentType {

        TEXT_UTF8("text/plain; charset=utf-8"),
        HTML_UTF8("text/html; charset=utf-8");

        private final String contentType;

        ContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public String toString() {
            return contentType;
        }

        public static ContentType get(String value) {
            for (ContentType contentType : ContentType.values()) {
                if (contentType.toString().equals(value)) {
                    return contentType;
                }
            }
            return null;
        }
    }

    private ArrayList<String> recipients = new ArrayList<>();
    private ArrayList<String> carbonCopyRecipients = new ArrayList<>();
    private ArrayList<String> blindCarbonCopyRecipients = new ArrayList<>();
    private ContentType contentType;
    private String title;
    private String body;
    private ArrayList<EmbeddedImageAttachment> embeddedImageAttachments = new ArrayList<>();
    private ArrayList<FileMailAttachment> fileAttachments = new ArrayList<>();

    public Collection<String> getRecipients() {
        return recipients;
    }

    public Collection<String> getCarbonCopyRecipients() {
        return carbonCopyRecipients;
    }

    public Collection<String> getBlindCarbonCopyRecipients() {
        return blindCarbonCopyRecipients;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public List<EmbeddedImageAttachment> getEmbeddedImageAttachments() {
        return embeddedImageAttachments;
    }

    public List<FileMailAttachment> getFileAttachments() {
        return fileAttachments;
    }

    public Mail withRecipients(ArrayList<String> recipients) {
        this.recipients = recipients;
        return this;
    }

    public Mail withRecipients(String... recipient) {
        this.recipients = prepareEmailAddresses(recipient);
        return this;
    }


    public Mail withCarbonCopyRecipients(ArrayList<String> carbonCopyRecipients) {
        this.carbonCopyRecipients = carbonCopyRecipients;
        return this;
    }

    public Mail withCarbonCopyRecipients(String... carbonCopyRecipient) {
        this.carbonCopyRecipients = prepareEmailAddresses(carbonCopyRecipient);
        return this;
    }

    public Mail withBlindCarbonCopyRecipients(ArrayList<String> blindCarbonCopyRecipients) {
        this.blindCarbonCopyRecipients = blindCarbonCopyRecipients;
        return this;
    }


    public Mail withBlindCarbonCopyRecipients(String... blindCarbonCopyRecipient) {
        this.blindCarbonCopyRecipients = prepareEmailAddresses(blindCarbonCopyRecipient);
        return this;
    }

    public Mail withContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public Mail withTitle(String title) {
        this.title = title;
        return this;
    }

    public Mail withBody(String body) {
        this.body = body;
        return this;
    }

    public Mail appendEmbeddedImageAttachments(EmbeddedImageAttachment... attachments) {
        if (attachments != null && attachments.length > 0) {
            this.embeddedImageAttachments.addAll(Arrays.asList(attachments));
        }
        return this;
    }

    public Mail appendFileAttachments(FileMailAttachment... attachments) {
        if (attachments != null && attachments.length > 0) {
            this.fileAttachments.addAll(Arrays.asList(attachments));
        }
        return this;
    }

    private static ArrayList<String> prepareEmailAddresses(String[] recipient) {
        return Arrays.stream(recipient)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
