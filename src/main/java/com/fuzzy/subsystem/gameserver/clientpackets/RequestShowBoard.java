package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.communitybbs.CommunityBoard;

public class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	public void readImpl()
	{
		_unknown = readD(); //always 1
	}

	@Override
	public void runImpl()
	{
		CommunityBoard.getInstance().handleCommands(getClient(), ConfigValue.BBSDefault);
	}
}