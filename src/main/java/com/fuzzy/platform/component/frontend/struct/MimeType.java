package com.fuzzy.platform.component.frontend.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public class MimeType implements RemoteObject {

    public static final MimeType TEXT = new MimeType("text/plain; charset=UTF-8");

    public static final MimeType HTML = new MimeType("text/html; charset=UTF-8");

    public static final MimeType CSS = new MimeType("text/css; charset=UTF-8");

    public static final MimeType JAVASCRIPT = new MimeType("text/javascript; charset=UTF-8");

    public static final MimeType PNG = new MimeType("image/png");

    public static final MimeType XLS = new MimeType("application/vnd.ms-excel");

    public static final MimeType XLSX = new MimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    public static final MimeType EXE = new MimeType("application/x-msdownload");

    public static final MimeType MSI = new MimeType("application/x-msi");

    public static final MimeType DMG = new MimeType("application/x-apple-diskimage");

    public static final MimeType ZIP = new MimeType("application/zip");

    public static final MimeType DEB = new MimeType("application/vnd.debian.binary-package");

    public static final MimeType RPM = new MimeType("application/x-rpm");


    public final String value;

    public MimeType(String value) {
        this.value = value;
    }
}
