package com.fuzzy.subsystem.extensions.listeners.events.L2Object;

import com.fuzzy.subsystem.extensions.listeners.events.DefaultMethodInvokeEvent;
import com.fuzzy.subsystem.gameserver.model.L2Object;


public class MethodInvokeEvent extends DefaultMethodInvokeEvent {

    public MethodInvokeEvent(String methodName, L2Object owner, Object[] args) {
        super(methodName, owner, args);
    }

    public L2Object getObject() {
        return (L2Object) getOwner();
    }
}
