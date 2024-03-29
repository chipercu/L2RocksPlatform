package com.fuzzy.subsystems.entityelements;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;

public interface EntityElementRecalculator {

    void recalculate(Long entityId, QueryTransaction transaction) throws PlatformException;

    void recalculate(QueryTransaction transaction) throws PlatformException;
}
