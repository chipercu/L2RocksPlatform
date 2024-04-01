package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.residence.ClanHall;
import com.fuzzy.subsystem.gameserver.model.entity.siege.clanhall.ClanHallSiege;
import com.fuzzy.subsystem.gameserver.model.entity.siege.territory.TerritorySiege;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.serverpackets.SiegeInfo;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2ClanHallMessengerInstance extends L2NpcInstance
{
	public L2ClanHallMessengerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		ClanHall clanhall = getClanHall();
		if(clanhall == null)
			return;
		ClanHallSiege siege = clanhall.getSiege();
		if(siege == null)
			return;
		if(siege.isInProgress() || siege.isRegistrationOver() || TerritorySiege.isInProgress())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setHtml("<html><body><font color=\"LEVEL\">I can't do anything for you right now.</font></body></html>");
			player.sendPacket(html);
		}
		else
			player.sendPacket(new SiegeInfo(getClanHall()));
	}
}
