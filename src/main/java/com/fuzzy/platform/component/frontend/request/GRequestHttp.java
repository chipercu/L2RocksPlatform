package com.fuzzy.platform.component.frontend.request;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.struct.GRequest;
import jakarta.servlet.http.Cookie;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class GRequestHttp extends GRequest {

    private final HashMap<String, String[]> parameters;
    private final HashMap<String, String[]> attributes;

    private final Cookie[] cookies;

    private final ArrayList<UploadFile> uploadFiles;

    public GRequestHttp(Instant instant, RemoteAddress remoteAddress, String query, HashMap<String, Serializable> queryVariables, String operationName, String xTraceId, HashMap<String, String[]> parameters, HashMap<String, String[]> attributes, Cookie[] cookies, ArrayList<UploadFile> uploadFiles) {
        super(instant, remoteAddress, query, queryVariables, operationName, xTraceId);

        this.parameters = parameters;

        this.attributes = attributes;

        this.cookies = cookies;

        this.uploadFiles = uploadFiles;
    }

    public String getParameter(String name) {
        String[] values = getParameters(name);
        return (values == null) ? null : values[0];
    }

    public String[] getParameters(String name) {
        return parameters.get(name);
    }

    public String[] getAttributes(String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    public Cookie getCookie(String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) return cookie;
            }
        }
        return null;
    }

    public ArrayList<UploadFile> getUploadFiles() {
        return uploadFiles;
    }

    public static class UploadFile implements RemoteObject {

        public final String fieldname;
        public final String filename;
        public final String contentType;
        public final URI uri;
        public final long size;

        public UploadFile(String fieldname, String filename, String contentType, URI uri, long size) {
            this.fieldname = fieldname;
            this.filename = filename;
            this.contentType = contentType;
            this.uri = uri;
            this.size = size;
        }
    }
}
