package com.fuzzy.subsystem.core.entityprovider.entity;

import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.entityprovider.datasources.SystemEventDataSource;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.infomaximum.subsystem.entityprovidersdk.entity.DataContainer;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityClass;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityField;
import com.infomaximum.subsystem.entityprovidersdk.entity.Id;
import com.infomaximum.subsystem.entityprovidersdk.enums.DataType;

import java.time.Instant;

@EntityClass(
        name = "system_event",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = SystemEventDataSource.class)
public class SystemEventEntity implements DataContainer {

    private final long id;
    private final SystemEvent systemEvent;

    public SystemEventEntity(long id, SystemEvent systemEvent) {
        this.id = id;
        this.systemEvent = systemEvent;
    }

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return id;
    }

    @EntityField(name = "event_type", type = DataType.STRING)
    public String getEventType() {
        return systemEvent.getEventType();
    }

    @EntityField(name = "time", type = DataType.INSTANT)
    public Instant getTime() {
        return systemEvent.getTime().toInstant();
    }

    @EntityField(name = "level", type = DataType.INTEGER)
    public int getLevel() {
        return systemEvent.getLevel().getLevel();
    }

    @EntityField(name = "subsystem_uuid", type = DataType.STRING)
    public String getSubsystemUuid() {
        return systemEvent.getSubsystemUuid();
    }

    @EntityField(name = "message", type = DataType.STRING)
    public String getMessage() {
        return systemEvent.getMessage();
    }

    @EntityField(name = "ttl", type = DataType.LONG)
    public Long getTtl() {
        return systemEvent.getTtl().toMillis();
    }
}
