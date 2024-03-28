package com.fuzzy.main.platform.sdk.context;

import com.fuzzy.main.platform.sdk.context.source.Source;

public interface Context<S extends Source> {

    S getSource();

    String getTrace();
}
