package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.gameserver.serverpackets.SkillCoolTime;

public class RequestSkillCoolTime extends L2GameClientPacket
{
	L2GameClient _client;

	@Override
	public void readImpl()
	{
		_client = getClient();
	}

	@Override
	public void runImpl()
	{
		L2Player pl = _client.getActiveChar();
		if(pl != null)
			pl.sendPacket(new SkillCoolTime(pl));
	}

	public boolean isFilter()
	{
		return false;
	}
}