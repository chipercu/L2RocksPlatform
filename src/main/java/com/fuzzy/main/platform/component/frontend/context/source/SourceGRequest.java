package com.fuzzy.main.platform.component.frontend.context.source;


import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.sdk.context.source.Source;

public interface SourceGRequest extends Source {

    GRequest getRequest();
}
