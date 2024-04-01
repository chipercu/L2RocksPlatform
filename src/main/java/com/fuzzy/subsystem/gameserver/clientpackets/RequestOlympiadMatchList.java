package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;
import com.fuzzy.subsystem.gameserver.serverpackets.ExOlympiadMatchList;

public class RequestOlympiadMatchList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if((!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd()) && !Olympiad.isFakeOly())
		{
			_log.info("RequestOlympiadMatchList: THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS");
			//player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}
		player.sendPacket(new ExOlympiadMatchList());
		//player.sendPacket(new ExReceiveOlympiad.MatchList());
	}
}