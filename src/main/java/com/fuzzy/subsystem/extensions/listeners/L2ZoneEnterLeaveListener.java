package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.L2Zone.L2ZoneEnterLeaveEvent;
import com.fuzzy.subsystem.extensions.listeners.events.MethodEvent;
import com.fuzzy.subsystem.gameserver.model.L2Object;
import com.fuzzy.subsystem.gameserver.model.L2Zone;

public abstract class L2ZoneEnterLeaveListener implements MethodInvokeListener, MethodCollection {
    /**
     * Вызывается при входе/выходе из зоны. После необходимых операций вызывает нужный
     * абстрактный метод.
     */
    @Override
    public final void methodInvoked(MethodEvent e) {
        L2ZoneEnterLeaveEvent event = (L2ZoneEnterLeaveEvent) e;
        L2Zone owner = event.getOwner();
        L2Object actor = event.getArgs()[0];
        if (e.getMethodName().equals(L2ZoneObjectEnter))
            objectEntered(owner, actor);
        else
            objectLeaved(owner, actor);
    }

    @Override
    public final boolean accept(MethodEvent event) {
        String method = event.getMethodName();
        return event instanceof L2ZoneEnterLeaveEvent && (method.equals(L2ZoneObjectEnter) || method.equals(L2ZoneObjectLeave));
    }

    /**
     * Метод вызывается когда объект входит в зону
     */
    public abstract void objectEntered(L2Zone zone, L2Object object);

    /**
     * Метод вызывается когда объект выходит с зоны
     * @param zone   зона с которой вышли
     * @param object вышедший объект
     */
    public abstract void objectLeaved(L2Zone zone, L2Object object);
}
