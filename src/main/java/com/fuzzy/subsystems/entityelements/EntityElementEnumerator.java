package com.fuzzy.subsystems.entityelements;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;

public interface EntityElementEnumerator<T> {

    void forEachNode(final Long entityId, QueryTransaction transaction, Function<T, Boolean> function) throws PlatformException;

    void forEachItem(final Long entityId, QueryTransaction transaction, Function<T, Boolean> function) throws PlatformException;

    boolean isAll(final Long entityId, QueryTransaction transaction) throws PlatformException;
}
