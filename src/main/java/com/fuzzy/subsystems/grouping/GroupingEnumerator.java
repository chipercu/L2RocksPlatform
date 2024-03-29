package com.fuzzy.subsystems.grouping;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;

public interface GroupingEnumerator {

    boolean forEachParent(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
            throws PlatformException;

    boolean forEachChild(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
            throws PlatformException;
}
