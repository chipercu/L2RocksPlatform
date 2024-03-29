package com.fuzzy.subsystems.textfilter;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.sdk.iterator.Iterator;
import com.fuzzy.subsystems.function.Consumer;

public interface TextFilterGetter<T> extends TextFilterEnumerator<T> {

    Iterator<T> findAll(String text, QueryTransaction transaction) throws PlatformException;

    default void forEach(String text, Consumer<T> handler, QueryTransaction transaction) throws PlatformException {
        try (Iterator<T> ie = findAll(text, transaction)) {
            while (ie.hasNext()) {
                handler.accept(ie.next());
            }
        }
    }
}
