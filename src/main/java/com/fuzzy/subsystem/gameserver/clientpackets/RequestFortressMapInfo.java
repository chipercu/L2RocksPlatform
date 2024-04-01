package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.FortressManager;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Fortress;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowFortressMapInfo;

public class RequestFortressMapInfo extends L2GameClientPacket
{
	private int fort_id;

	@Override
	public void readImpl()
	{
		fort_id = readD();
	}

	@Override
	public void runImpl()
	{
		Fortress fortress = FortressManager.getInstance().getFortressByIndex(fort_id);
		if(fortress != null)
			sendPacket(new ExShowFortressMapInfo(fortress));
	}
}