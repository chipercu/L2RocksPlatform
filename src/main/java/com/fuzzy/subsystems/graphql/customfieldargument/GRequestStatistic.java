package com.fuzzy.subsystems.graphql.customfieldargument;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import com.fuzzy.main.platform.component.frontend.engine.service.statistic.StatisticService;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystems.Subsystems;

import java.lang.reflect.Method;

public class GRequestStatistic implements RemoteObject {

    public static class Builder implements CustomFieldArgument<GRequestStatistic> {

        private final Subsystems subsystems;

        public Builder(Subsystems subsystems) {
            this.subsystems = subsystems;
        }

        @Override
        public boolean isSupport(Class classType) {
            return classType == GRequestStatistic.class;
        }

        @Override
        public GRequestStatistic getValue(Class classType, Method method, ContextRequest context) {
            FrontendSubsystem frontEndSubsystem = subsystems.getCluster().getAnyLocalComponent(FrontendSubsystem.class);
            StatisticService statisticService = frontEndSubsystem.getStatisticService();
            return new GRequestStatistic(statisticService.getQueueDownloadBytes());
        }
    }

    private final long queueDownloadBytes;

    private GRequestStatistic(long queueDownloadBytes) {
        this.queueDownloadBytes = queueDownloadBytes;
    }

    public long getQueueDownloadBytes() {
        return queueDownloadBytes;
    }
}
