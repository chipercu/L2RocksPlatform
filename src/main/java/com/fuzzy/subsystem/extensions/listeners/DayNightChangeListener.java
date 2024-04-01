package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.PropertyEvent;
import com.fuzzy.subsystem.gameserver.GameTimeController;

public abstract class DayNightChangeListener implements PropertyChangeListener, PropertyCollection {

    /**
     * Вызывается при смене состояния
     *
     * @param event передаваемое событие
     */
    @Override
    public final void propertyChanged(PropertyEvent event) {
        if (((GameTimeController) event.getObject()).isNowNight())
            switchToNight();
        else
            switchToDay();
    }

    @Override
    public final boolean accept(String property) {
        return GameTimeControllerDayNightChange.equals(property);
    }

    /**
     * Возвращает свойство даного листенера
     *
     * @return свойство
     */
    @Override
    public final String getPropery() {
        return GameTimeControllerDayNightChange;
    }

    /**
     * Вызывается когда на сервер наступает ночь
     */
    public abstract void switchToNight();

    /**
     * Вызывается когда на сервере наступает день
     */
    public abstract void switchToDay();
}
