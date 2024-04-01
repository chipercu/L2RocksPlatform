package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.siege.territory.TerritorySiege;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2CastleMessengerInstance extends L2NpcInstance
{
	public L2CastleMessengerInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	/**
	 * If siege is in progress shows the Busy HTML<BR>
	 * else Shows the SiegeInfo window
	 * @param player
	 */
	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(!getCastle().getSiege().isInProgress() && !TerritorySiege.isInProgress())
			getCastle().getSiege().listRegisterClan(player);
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setHtml("<html><body><font color=\"LEVEL\">Oh! Our castle is being attacked and I can't do anything for you right now.</font></body></html>");
			player.sendPacket(html);
		}
	}
}