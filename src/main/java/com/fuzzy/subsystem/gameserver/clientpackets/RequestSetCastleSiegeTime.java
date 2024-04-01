package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.CastleManager;
import com.fuzzy.subsystem.gameserver.model.L2Clan;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.serverpackets.*;

public class RequestSetCastleSiegeTime extends L2GameClientPacket
{
	private int _id;
	private int _time;

	@Override
	public void readImpl()
	{
		_id = readD();
		_time = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Castle castle = CastleManager.getInstance().getCastleByIndex(_id);
		if(castle == null)
			return;

		if(player.getClan().getHasCastle() != castle.getId())
			return;

		if((player.getClanPrivileges() & L2Clan.CP_CS_MANAGE_SIEGE) != L2Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME));
			return;
		}

		castle.getSiege().setNextSiegeTime(_time);

		player.sendPacket(new SiegeInfo(castle));
	}
}