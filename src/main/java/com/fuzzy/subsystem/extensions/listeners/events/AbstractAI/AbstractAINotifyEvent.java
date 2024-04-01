package com.fuzzy.subsystem.extensions.listeners.events.AbstractAI;

import com.fuzzy.subsystem.extensions.listeners.events.DefaultMethodInvokeEvent;
import com.fuzzy.subsystem.gameserver.ai.AbstractAI;

public class AbstractAINotifyEvent extends DefaultMethodInvokeEvent {
    public AbstractAINotifyEvent(String methodName, AbstractAI owner, Object[] args) {
        super(methodName, owner, args);
    }

    @Override
    public AbstractAI getOwner() {
        return (AbstractAI) super.getOwner();
    }

    @Override
    public Object[] getArgs() {
        return super.getArgs();
    }
}