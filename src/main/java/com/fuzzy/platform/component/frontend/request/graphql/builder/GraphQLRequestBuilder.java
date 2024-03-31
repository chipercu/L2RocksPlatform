package com.fuzzy.platform.component.frontend.request.graphql.builder;

import com.fuzzy.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.fuzzy.platform.component.frontend.request.graphql.GraphQLRequest;
import com.fuzzy.platform.exception.PlatformException;
import jakarta.servlet.http.HttpServletRequest;

public interface GraphQLRequestBuilder {

    GraphQLRequest build(HttpServletRequest request) throws PlatformException;

    abstract class Builder {

        public abstract GraphQLRequestBuilder build(FrontendMultipartSource frontendMultipartSource);

    }

}
