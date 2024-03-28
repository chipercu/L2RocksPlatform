package com.fuzzy.main.platform.sdk.struct;

import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Path;

public class ClusterFile extends com.fuzzy.main.cluster.core.io.ClusterFile {

    public ClusterFile(Component component, URI uri) {
        super(component, uri);
    }

    public void copyTo(Path file, CopyOption... options) throws PlatformException {
        try {
            super.copyTo(file, options);
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public void copyTo(OutputStream target) throws PlatformException {
        try {
            super.copyTo(target);
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public void delete() throws PlatformException {
        try {
            super.delete();
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public void deleteIfExists() throws PlatformException {
        try {
            super.deleteIfExists();
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public void moveTo(Path target, CopyOption... options) throws PlatformException {
        try {
            super.moveTo(target, options);
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public long getSize() throws PlatformException {
        try {
            return super.getSize();
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    public byte[] getContent() throws PlatformException {
        try {
            return super.getContent();
        } catch (Exception e) {
            throw toPlatformException(e);
        }
    }

    private static PlatformException toPlatformException(Exception e) {
        if (e instanceof PlatformException pe) {
            return pe;
        } else if (e instanceof IOException ioe) {
            return GeneralExceptionBuilder.buildIOErrorException(ioe);
        } else {
            throw new RuntimeException(e);
        }
    }
}
