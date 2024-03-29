package com.fuzzy.subsystem.core.remote.mail;

/**
 * Created by v.bukharkin on 19.05.2017.
 */
public class FileMailAttachment extends MailAttachment {

    public FileMailAttachment(String name, String contentType, byte[] body) {
        super(name, contentType, body, false);
    }
}
