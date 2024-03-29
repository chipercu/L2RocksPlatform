package com.fuzzy.subsystems.graphql.customfieldargument;

import com.infomaximum.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.infomaximum.cluster.graphql.struct.ContextRequest;
import com.infomaximum.cluster.struct.Component;
import com.fuzzy.main.Subsystems;
import com.fuzzy.subsystems.subsystem.Subsystem;

import java.lang.reflect.Method;

public class SubsystemEnvironment implements CustomFieldArgument<Component> {

    private final Subsystems subsystems;

    public SubsystemEnvironment(Subsystems subsystems) {
        this.subsystems = subsystems;
    }

    @Override
    public boolean isSupport(Class classType) {
        return (Subsystem.class.isAssignableFrom(classType));
    }

    @Override
    public Component getValue(Class classType, Method method, ContextRequest context) {
        return subsystems.getCluster().getAnyLocalComponent(classType);
    }

}
