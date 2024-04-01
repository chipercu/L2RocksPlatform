package com.fuzzy.subsystem.extensions.listeners.events.L2Object;

import com.fuzzy.subsystem.extensions.listeners.events.DefaultPropertyChangeEvent;
import com.fuzzy.subsystem.gameserver.model.L2Object;

public class PropertyChangeEvent extends DefaultPropertyChangeEvent {

    public PropertyChangeEvent(String event, L2Object actor, Object oldV, Object newV) {
        super(event, actor, oldV, newV);
    }

    @Override
    public L2Object getObject() {
        return (L2Object) super.getObject();
    }
}
