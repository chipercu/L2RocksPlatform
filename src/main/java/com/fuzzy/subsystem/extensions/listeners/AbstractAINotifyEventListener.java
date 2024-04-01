package com.fuzzy.subsystem.extensions.listeners;

import com.fuzzy.subsystem.extensions.listeners.events.AbstractAI.AbstractAINotifyEvent;
import com.fuzzy.subsystem.extensions.listeners.events.MethodEvent;
import com.fuzzy.subsystem.gameserver.ai.AbstractAI;
import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;

public abstract class AbstractAINotifyEventListener implements MethodInvokeListener, MethodCollection {
    @Override
    public final void methodInvoked(MethodEvent e) {
        AbstractAINotifyEvent event = (AbstractAINotifyEvent) e;
        AbstractAI ai = event.getOwner();
        CtrlEvent evt = (CtrlEvent) event.getArgs()[0];
        NotifyEvent(ai, evt, (Object[]) event.getArgs()[1]);
    }

    @Override
    public final boolean accept(MethodEvent event) {
        String method = event.getMethodName();
        return event instanceof AbstractAINotifyEvent && method.equals(AbstractAInotifyEvent);
    }

    public abstract void NotifyEvent(AbstractAI ai, CtrlEvent evt, Object[] args);
}
