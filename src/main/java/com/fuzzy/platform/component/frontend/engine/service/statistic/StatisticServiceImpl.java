package com.fuzzy.platform.component.frontend.engine.service.statistic;

import com.fuzzy.platform.component.frontend.engine.service.statistic.StatisticService;
import com.fuzzy.platform.component.frontend.engine.service.statistic.listener.HttpRequestListener;

public class StatisticServiceImpl implements StatisticService {

    private final HttpRequestListener listener;

    public StatisticServiceImpl() {
        listener = new HttpRequestListener();
    }

    public HttpRequestListener getListener() {
        return listener;
    }

    @Override
    public long getQueueDownloadBytes() {
        return listener.getQueueDownloadBytes();
    }

}
