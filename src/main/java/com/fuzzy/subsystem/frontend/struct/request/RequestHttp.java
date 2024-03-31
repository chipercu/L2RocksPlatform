package com.fuzzy.subsystem.frontend.struct.request;

import com.fuzzy.platform.component.frontend.request.GRequestHttp;
import jakarta.servlet.http.Cookie;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestHttp extends GRequestHttp {

    private final String sessionUuid;

    public RequestHttp(Instant instant, RemoteAddress remoteAddress, HashMap<String, String[]> parameters, HashMap<String, String[]> attributes, Cookie[] cookies, ArrayList<UploadFile> uploadFiles, String sessionUuid) {
        super(instant, remoteAddress, null, null, null, null, parameters, attributes, cookies, uploadFiles);
        this.sessionUuid = sessionUuid;
    }

    public RequestHttp(Instant instant, RemoteAddress remoteAddress, HashMap<String, String[]> parameters, String sessionUuid) {
        super(instant, remoteAddress, null, null, null,null, parameters, null, null, null);
        this.sessionUuid = sessionUuid;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }
}
