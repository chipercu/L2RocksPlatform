package com.fuzzy.subsystem.extensions.network;

public interface IClientFactory<T extends MMOClient>
{
	public T create(MMOConnection<T> con);
}