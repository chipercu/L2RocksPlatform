package com.fuzzy.subsystem.extensions.network;


public interface IMMOExecutor<T extends MMOClient>
{
	public void execute(Runnable r);
}