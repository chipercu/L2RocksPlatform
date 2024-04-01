package com.fuzzy.subsystem.gameserver.handler;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface ICommunityHandler
{
	public void parsecmd(String command, L2Player activeChar);
	//public int getCommunityName();
	public Enum[] getCommunityCommandEnum();
}