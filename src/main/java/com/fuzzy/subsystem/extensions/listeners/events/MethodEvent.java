package com.fuzzy.subsystem.extensions.listeners.events;

public interface MethodEvent {

    Object getOwner();

    Object[] getArgs();

    String getMethodName();
}
