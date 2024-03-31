package com.fuzzy.subsystem.core.remote.timeline;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.subsystems.graphql.input.datetime.GDateInterval;
import com.fuzzy.subsystems.graphql.input.datetime.GInputDate;

import java.util.ArrayList;

public interface RCTimelineGetter extends QueryRemoteController {

    /**
     * @return несортированный список таймлайнов
     * @throws PlatformException
     */
    ArrayList<Timeline> get(ArrayList<TimeOffsetEmployee> employees,
                            GDateInterval dateFilter,
                            ContextTransactionRequest context) throws PlatformException;

    /**
     * @return Таймлайн по сотруднику за один день
     * @throws PlatformException
     */
    Timeline get(TimeOffsetEmployee employee,
                 GInputDate dateFilter,
                 ContextTransactionRequest context) throws PlatformException;
}
