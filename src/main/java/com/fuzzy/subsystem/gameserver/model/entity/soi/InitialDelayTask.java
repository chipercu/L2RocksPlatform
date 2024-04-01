package com.fuzzy.subsystem.gameserver.model.entity.soi;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public class InitialDelayTask extends com.fuzzy.subsystem.common.RunnableImpl
{
	private L2Player _player = null;
	private SeedOfInfinity _soi = null;

	public InitialDelayTask(L2Player player, SeedOfInfinity soi)
	{
		_player = player;
		_soi = soi;
	}

	@Override
	public void runImpl()
	{
		if(_player != null)
			_soi.initialInstance(_player);
	}
}