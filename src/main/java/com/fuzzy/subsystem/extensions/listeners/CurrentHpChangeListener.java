package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.PropertyEvent;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public abstract class CurrentHpChangeListener implements PropertyChangeListener, PropertyCollection {

    @Override
    public final void propertyChanged(PropertyEvent event) {
        onCurrentHpChange((L2Character) event.getObject(), (Double) event.getOldValue(), (Double) event.getNewValue());
    }

    @Override
    public final boolean accept(String property) {
        return HitPoints.equals(property);
    }

    @Override
    public final String getPropery() {
        return HitPoints;
    }

    public abstract void onCurrentHpChange(L2Character actor, double oldHp, double newHp);
}
