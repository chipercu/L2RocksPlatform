package com.fuzzy.subsystem.gameserver.model;

import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;

public interface SpawnListener
{
	public void npcSpawned(L2NpcInstance npc);

	public void npcDeSpawned(L2NpcInstance npc);
}
