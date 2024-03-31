package com.fuzzy.subsystem.core.subscription.employee;

import com.fuzzy.cluster.graphql.struct.GSubscribeEvent;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.subscription.SubscriptionType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class GEmployeeUpdateEvent extends GSubscribeEvent<Boolean> {

    public GEmployeeUpdateEvent(CoreSubsystem component, long employeeId) {
        super(getSubscriptionKey(component, employeeId), true);
    }

    private static byte[] getSubscriptionKey(CoreSubsystem component, long employeeId) {
        UUID nodeRuntimeId = component.getRemotes().cluster.node.getRuntimeId();
        return ByteBuffer.allocate(Long.BYTES + Long.BYTES + Integer.BYTES + Integer.BYTES + Long.BYTES)
                .putLong(nodeRuntimeId.getMostSignificantBits())
                .putLong(nodeRuntimeId.getLeastSignificantBits())
                .putInt(component.getId())
                .putInt(SubscriptionType.EMPLOYEE_UPDATE.intValue())
                .putLong(employeeId)
                .array();
    }

    public static void send(CoreSubsystem component, long employeeId, QueryTransaction transaction) {
        component.getGraphQLSubscribeEvent().push(new GEmployeeUpdateEvent(component, employeeId), transaction);
    }
}
