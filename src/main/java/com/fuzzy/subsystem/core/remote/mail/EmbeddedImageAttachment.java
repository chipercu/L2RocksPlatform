package com.fuzzy.subsystem.core.remote.mail;

public class EmbeddedImageAttachment extends MailAttachment {

    public EmbeddedImageAttachment(String name, String contentType, byte[] body) {
        super(name, contentType, body, true);
    }
}