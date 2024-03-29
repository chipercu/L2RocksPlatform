package com.fuzzy.subsystem.core.entityprovider.entity;

import com.fuzzy.main.platform.service.detectresource.PlatformEventType;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.entityprovider.datasources.ResourceMonitorDataSource;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.EntityClass;
import com.fuzzy.subsystem.entityprovidersdk.entity.EntityField;
import com.fuzzy.subsystem.entityprovidersdk.entity.Id;
import com.fuzzy.subsystem.entityprovidersdk.enums.DataType;

import java.util.Map;

@EntityClass(
        name = "resource_monitor",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = ResourceMonitorDataSource.class
)
public class ResourceMonitorEntity implements DataContainer {

    private final long id;
    private final Map<PlatformEventType, SystemEvent> events;

    public ResourceMonitorEntity(long id, Map<PlatformEventType, SystemEvent> events) {
        this.id = id;
        this.events = events;
    }

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return id;
    }

    @EntityField(name = "cpu", type = DataType.DOUBLE)
    public double getCpuLoad() {
        double result = -1;
        SystemEvent event = events.getOrDefault(PlatformEventType.CPU_JVM_MONITORING, events.get(PlatformEventType.CPU_HOST_MONITORING));
        if (event != null) {
            result = getParamValue(event);
        }
        return result;
    }

    @EntityField(name = "ram", type = DataType.DOUBLE)
    public double getRamUsed() {
        double result = -1;
        SystemEvent event = events.getOrDefault(PlatformEventType.MEMORY_JVM_MONITORING, events.get(PlatformEventType.MEMORY_HOST_MONITORING));
        if (event != null) {
            result = getParamValue(event);
        }
        return result;
    }

    @EntityField(name = "disk", type = DataType.DOUBLE)
    public double getDiskSpaceUsed() {
        double result = -1;
        SystemEvent event = events.getOrDefault(PlatformEventType.DISK_JVM_MONITORING, events.get(PlatformEventType.DISK_HOST_MONITORING));
        if (event != null) {
            result = getParamValue(event);
        }
        return result;
    }

    private double getParamValue(SystemEvent event) {
        return event
                .getParams()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(event.getEventType()))
                .map(entry -> (double) entry.getValue())
                .findAny()
                .orElse(-1.);
    }
}
