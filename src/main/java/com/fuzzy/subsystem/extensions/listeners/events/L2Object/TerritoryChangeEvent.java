package com.fuzzy.subsystem.extensions.listeners.events.L2Object;

import com.fuzzy.subsystem.extensions.listeners.PropertyCollection;
import com.fuzzy.subsystem.extensions.listeners.events.PropertyEvent;
import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.gameserver.model.L2Territory;

import java.util.Collection;


public class TerritoryChangeEvent implements PropertyEvent {
    private final Collection<L2Territory> enter;
    private final Collection<L2Territory> exit;
    private final L2Object object;

    public TerritoryChangeEvent(Collection<L2Territory> enter, Collection<L2Territory> exit, L2Object object) {
        this.enter = enter;
        this.exit = exit;
        this.object = object;
    }

    @Override
    public L2Object getObject() {
        return object;
    }

    /**
     * Возврщает список территорий с которых вышел объект
     *
     * @return список удаленных территорий
     */
    @Override
    public Collection<L2Territory> getOldValue() {
        return exit;
    }

    /**
     * Возвразает список территорой в которые вошел объект
     *
     * @return список добавленых территорий
     */
    @Override
    public Collection<L2Territory> getNewValue() {
        return enter;
    }

    @Override
    public String getProperty() {
        return PropertyCollection.TerritoryChanged;
    }
}
