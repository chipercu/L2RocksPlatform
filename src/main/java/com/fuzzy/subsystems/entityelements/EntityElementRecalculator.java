package com.fuzzy.subsystems.entityelements;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;

public interface EntityElementRecalculator {

    void recalculate(Long entityId, QueryTransaction transaction) throws PlatformException;

    void recalculate(QueryTransaction transaction) throws PlatformException;
}
