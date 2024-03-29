package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.entityprovider.entity.SystemEventEntity;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;
import com.infomaximum.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SystemEventDataSource implements DataSourceProvider<SystemEventEntity> {

    private SystemEventService systemEventService;

    private long id = 0;

    @Override
    public void prepare(ResourceProvider resources) {
        CoreSubsystem subsystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(CoreSubsystem.class);
        systemEventService = subsystem.getSystemEventService();
    }

    @Override
    public DataSourceIterator<SystemEventEntity> createIterator(long lastProcessedId, int limit, QueryTransaction transaction) throws PlatformException {
        final List<SystemEventEntity> result = new ArrayList<>();
        if (lastProcessedId > 0) {
            return new BaseSourceIterator<>(result);
        }

        final ZonedDateTime now = ZonedDateTime.now();
        for (SystemEvent systemEvent : systemEventService.getActualEvents(now)) {
            result.add(map(systemEvent));
        }
        return new BaseSourceIterator<>(result);
    }

    private SystemEventEntity map(SystemEvent systemEvent) {
        return new SystemEventEntity(++id, systemEvent);
    }
}
