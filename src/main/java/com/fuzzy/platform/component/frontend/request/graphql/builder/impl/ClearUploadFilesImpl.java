package com.fuzzy.platform.component.frontend.request.graphql.builder.impl;

import com.fuzzy.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.fuzzy.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.platform.component.frontend.request.graphql.builder.ClearUploadFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClearUploadFilesImpl implements ClearUploadFiles {

    private final static Logger log = LoggerFactory.getLogger(ClearUploadFilesImpl.class);

    private final FrontendMultipartSource frontendMultipartSource;

    public ClearUploadFilesImpl(FrontendMultipartSource frontendMultipartSource) {
        this.frontendMultipartSource = frontendMultipartSource;
    }

    @Override
    public void clear(List<GRequestHttp.UploadFile> uploadFiles) {
        if (uploadFiles == null) return;
        for (GRequestHttp.UploadFile uploadFile : uploadFiles) {
            try {
                frontendMultipartSource.remove(uploadFile.uri);
            } catch (Throwable t) {
                log.error("Error clear upload file", t);
            }
        }
    }
}
