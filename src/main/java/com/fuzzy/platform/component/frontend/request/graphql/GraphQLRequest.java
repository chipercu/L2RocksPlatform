package com.fuzzy.platform.component.frontend.request.graphql;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.platform.component.frontend.request.graphql.builder.ClearUploadFiles;

public class GraphQLRequest implements AutoCloseable {

    private final GRequestHttp gRequest;
    private final ClearUploadFiles clearUploadFiles;

    public GraphQLRequest(
            GRequestHttp gRequest,
            ClearUploadFiles clearUploadFiles
    ) {
        this.gRequest = gRequest;
        this.clearUploadFiles = clearUploadFiles;
    }

    public GRequest getGRequest() {
        return gRequest;
    }

    @Override
    public void close() {
        clearUploadFiles.clear(gRequest.getUploadFiles());
    }
}
