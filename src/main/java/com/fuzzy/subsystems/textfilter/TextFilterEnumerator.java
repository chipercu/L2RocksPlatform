package com.fuzzy.subsystems.textfilter;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Consumer;

public interface TextFilterEnumerator<T> {

    void forEach(String text, Consumer<T> handler, QueryTransaction transaction) throws PlatformException;
}
