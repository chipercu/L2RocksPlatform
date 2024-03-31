package com.fuzzy.platform.component.frontend.context.source;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.sdk.context.source.Source;

public interface SourceGRequest extends Source {

    GRequest getRequest();
}
