package com.fuzzy.main.platform.component.frontend.request.graphql.builder.impl.attribute;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

public class GraphQLRequestAttributeBuilderEmpty implements GraphQLRequestAttributeBuilder {

    @Override
    public HashMap<String, String[]> build(HttpServletRequest request) {
        return null;
    }
}
