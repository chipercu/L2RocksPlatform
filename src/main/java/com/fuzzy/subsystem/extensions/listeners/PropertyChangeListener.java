package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.PropertyEvent;

public interface PropertyChangeListener {
    /**
     * Вызывается при смене состояния
     *
     * @param event передаваемое событие
     */
    void propertyChanged(PropertyEvent event);

    /**
     * Возвращает свойство даного листенера
     *
     * @return свойство
     */
    String getPropery();

    /**
     * Простенький фильтр, если лень добавлят по группам.
     * <p>
     * Фильтр применяется только в общем хранилеще, тоесть при:
     * <p>
     * addPropertyChangeListener(слушатель) - обработается этот метод
     * <p>
     * а при
     * <p>
     * addPropertyChangeListener(параметр, слушатель) не обработает
     *
     * @param property свойство
     * @return принимать ли
     */
    boolean accept(String property);
}
