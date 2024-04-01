package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Location;

public class ObserverEnd extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverEnd(L2Player observer)
	{
		_loc = observer.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xec);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}