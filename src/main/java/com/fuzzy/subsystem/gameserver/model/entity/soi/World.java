package com.fuzzy.subsystem.gameserver.model.entity.soi;

import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;

import java.util.Map;


public abstract class World
{
	public long timer;
	public int instanceId;
	public int status;
	public abstract void createNpcList();

	public abstract Map<L2NpcInstance, Boolean> getNpcList();
	public abstract void add(L2NpcInstance paramL2NpcInstance, boolean paramBoolean);
}