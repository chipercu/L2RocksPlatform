package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequstBR_LectureMark extends L2GameClientPacket
{
	private int mark;

	@Override
	protected void readImpl() throws Exception
	{
		mark = readC();
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player player = getClient().getActiveChar();
		_log.info("RequstBR_LectureMark [" + mark + "] from " + player);
	}
}