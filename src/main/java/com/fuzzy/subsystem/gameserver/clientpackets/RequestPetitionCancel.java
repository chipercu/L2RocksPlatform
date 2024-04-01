package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Log;

public class RequestPetitionCancel extends L2GameClientPacket
{
	private String _text;

	@Override
	public void readImpl()
	{
		_text = readS(4096);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		Log.LogPetition(activeChar, 0, "Cancel: " + _text);
	}
}