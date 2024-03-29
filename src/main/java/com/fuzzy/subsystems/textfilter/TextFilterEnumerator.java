package com.fuzzy.subsystems.textfilter;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Consumer;

public interface TextFilterEnumerator<T> {

    void forEach(String text, Consumer<T> handler, QueryTransaction transaction) throws PlatformException;
}
