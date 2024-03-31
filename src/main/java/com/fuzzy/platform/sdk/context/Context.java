package com.fuzzy.platform.sdk.context;

import com.fuzzy.platform.sdk.context.source.Source;

public interface Context<S extends Source> {

    S getSource();

    String getTrace();
}
