package com.fuzzy.subsystem.extensions.network;

public interface Lockable {
    /**
     * Lock for access
     */
    void lock();

    /**
     * Unlock after access
     */
    void unlock();
}
