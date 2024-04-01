package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManager;
import com.fuzzy.subsystem.gameserver.model.L2Multisell;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.siege.territory.TerritorySiege;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2TerritoryManagerInstance extends L2NpcInstance
{
	public L2TerritoryManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int npcId = getNpcId();
		int terr = npcId - 36489;
		if(terr > 9 || terr < 1)
			return;

		int territoryBadgeId = 13756 + terr;

		if(command.equalsIgnoreCase("buyspecial"))
		{
			if(Functions.getItemCount(player, territoryBadgeId) < 1)
				showChatWindow(player, getHtmlPath(npcId, 1));
			else
				L2Multisell.getInstance().SeparateAndSend(npcId, player, 0);
			return;
		}

		if(command.equalsIgnoreCase("calculate"))
		{
			int[] rewards = TerritorySiege.calculateReward(player, terr);
			if(rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, getHtmlPath(npcId, 4));
				return;
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this, getHtmlPath(npcId, 5), 5);
			html.replace("%territory%", CastleManager.getInstance().getCastleByIndex(terr).getName());
			html.replace("%badges%", String.valueOf(rewards[0]));
			html.replace("%adena%", String.valueOf(rewards[1]));
			html.replace("%fame%", String.valueOf(rewards[2]));
			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("recivelater"))
			showChatWindow(player, getHtmlPath(npcId, 6));
		else if(command.equalsIgnoreCase("recive"))
		{
			int[] rewards = TerritorySiege.calculateReward(player, terr);
			if(rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, getHtmlPath(npcId, 4));
				return;
			}
			if(TerritorySiege.isInProgress())
				return;

			Functions.addItem(player, territoryBadgeId, rewards[0]);
			Functions.addItem(player, 57, rewards[1]);
			//Functions.addItem(player, 57, rewards[0] * 520);
			if(rewards[2] > 0)
				player.setFame(player.getFame() + rewards[2], "CalcBadges:" + terr);
			TerritorySiege.clearReward(player.getObjectId());

			showChatWindow(player, getHtmlPath(npcId, 7));
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		if(val == 0)
			return "data/html/TerritoryManager/TerritoryManager.htm";
		return "data/html/TerritoryManager/TerritoryManager-" + val + ".htm";
	}
}