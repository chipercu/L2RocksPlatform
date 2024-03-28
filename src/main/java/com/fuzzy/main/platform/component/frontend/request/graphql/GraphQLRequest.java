package com.fuzzy.main.platform.component.frontend.request.graphql;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.component.frontend.request.graphql.builder.ClearUploadFiles;

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
