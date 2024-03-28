package com.fuzzy.subsystems.grouping;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;

public interface GroupingEnumerator {

    boolean forEachParent(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
            throws PlatformException;

    boolean forEachChild(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
            throws PlatformException;
}
