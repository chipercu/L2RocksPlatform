package com.fuzzy.subsystem.extensions.listeners.events.L2Zone;

import com.fuzzy.subsystem.extensions.listeners.events.DefaultMethodInvokeEvent;
import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.gameserver.model.L2Zone;

public class L2ZoneEnterLeaveEvent extends DefaultMethodInvokeEvent {
    public L2ZoneEnterLeaveEvent(String methodName, L2Zone owner, L2Object[] args) {
        super(methodName, owner, args);
    }

    @Override
    public L2Zone getOwner() {
        return (L2Zone) super.getOwner();
    }

    @Override
    public L2Object[] getArgs() {
        return (L2Object[]) super.getArgs();
    }
}
