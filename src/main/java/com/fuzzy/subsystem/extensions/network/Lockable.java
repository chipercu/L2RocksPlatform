package com.fuzzy.subsystem.extensions.network;

public interface Lockable
{
	/**
	 * Lock for access
	 */
	public void lock();
	/**
	 * Unlock after access
	 */
	public void unlock();
}
