package com.fuzzy.subsystem.core.entityprovider.datasources;

import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.entityprovider.entity.ResourceMonitorEntity;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;
import com.infomaximum.subsystem.entityprovidersdk.entity.BaseSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceIterator;
import com.infomaximum.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ResourceMonitorDataSource implements DataSourceProvider<ResourceMonitorEntity> {

    private SystemEventService systemEventService;

    @Override
    public void prepare(ResourceProvider resourceProvider) {
        systemEventService = Subsystems.getInstance().getCluster().getAnyLocalComponent(CoreSubsystem.class).getSystemEventService();
    }

    @Override
    public DataSourceIterator<ResourceMonitorEntity> createIterator(long lastProcessId, int limit, QueryTransaction queryTransaction) throws PlatformException {
        ArrayList<ResourceMonitorEntity> result = new ArrayList<>();
        if (lastProcessId > 0) {
            return new BaseSourceIterator<>(result);
        }
        EnumMap<PlatformEventType, SystemEvent> eventMap = new EnumMap<>(PlatformEventType.class);
        systemEventService.getEvents().forEach(event -> eventMap.put(PlatformEventType.valueOf(event.getEventType()), event));
        return new BaseSourceIterator<>(List.of(new ResourceMonitorEntity(System.currentTimeMillis(), eventMap)));
    }
}
