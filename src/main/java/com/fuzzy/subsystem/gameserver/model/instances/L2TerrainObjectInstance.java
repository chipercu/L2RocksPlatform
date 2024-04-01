package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public final class L2TerrainObjectInstance extends L2NpcInstance
{
	public L2TerrainObjectInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setHideName(true);
	}
	
	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		player.sendActionFailed();
	}
}