package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.FortressManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Fortress;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowFortressSiegeInfo;

public class RequestFortressSiegeInfo extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		for(Fortress fort : FortressManager.getInstance().getFortresses().values())
			if(fort != null && fort.getSiege().isInProgress())
				activeChar.sendPacket(new ExShowFortressSiegeInfo(fort));
	}
}