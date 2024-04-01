package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.MethodEvent;

public interface MethodInvokeListener {
    public void methodInvoked(MethodEvent e);

    /**
     * Простенький фильтр. Фильтрирует по названии метода и аргументам.
     * Ничто не мешает переделать при нужде :)
     *
     * @param event событие с аргументами
     * @return true если все ок ;)
     */
    public boolean accept(MethodEvent event);
}
