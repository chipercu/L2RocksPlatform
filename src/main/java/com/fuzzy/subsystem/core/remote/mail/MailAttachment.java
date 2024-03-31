package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public abstract class MailAttachment implements RemoteObject {

    private final String name;

    private final String contentType;

    private final byte[] body;

    private boolean embeddedImage;

    MailAttachment(String name, String contentType, byte[] body, boolean embeddedImage) {
        this.name = name;
        this.body = body;
        this.contentType = contentType;
        this.embeddedImage = embeddedImage;
    }

    public String getName() {
        return name;
    }

    public byte[] getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isEmbeddedImage() {
        return embeddedImage;
    }
}
