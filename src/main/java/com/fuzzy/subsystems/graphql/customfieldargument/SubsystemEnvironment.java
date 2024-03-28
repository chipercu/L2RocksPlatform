package com.fuzzy.subsystems.graphql.customfieldargument;

import com.fuzzy.main.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.subsystems.Subsystems;
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
