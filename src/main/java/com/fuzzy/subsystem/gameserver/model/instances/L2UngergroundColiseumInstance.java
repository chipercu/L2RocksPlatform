package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.instancemanager.UnderGroundColliseumManager;
import com.fuzzy.subsystem.gameserver.model.L2Multisell;
import com.fuzzy.subsystem.gameserver.model.L2Party;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.base.Experience;
import com.fuzzy.subsystem.gameserver.model.entity.Coliseum;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Log;

import java.util.StringTokenizer;

public class L2UngergroundColiseumInstance extends L2NpcInstance
{
	public L2UngergroundColiseumInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private int getMinLevel()
	{
		if(getNpcId() == 32513)
			return 40;
		if(getNpcId() == 32516)
			return 50;
		if(getNpcId() == 32515)
			return 60;
		if(getNpcId() == 32514)
			return 70;
		if(getNpcId() == 32377)
			return 1;
		return 1;
	}

	private int getMaxLevel()
	{
		if(getNpcId() == 32513)
			return 49;
		if(getNpcId() == 32516)
			return 59;
		if(getNpcId() == 32515)
			return 69;
		if(getNpcId() == 32514)
			return 79;
		if(getNpcId() == 32377)
			return 85;
		return Experience.getMaxLevel();
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "data/html/Coliseum/" + val + ".htm";
		NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, val);
		html.replace("%levelMin%", "" + getMinLevel());
		html.replace("%levelMax%", "" + getMaxLevel());
		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		player.sendActionFailed();

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if(actualCommand.startsWith("register"))
		{
			if(player.getParty() == null)
			{
				showChatWindow(player, 3);
				return;
			}

			if(!player.getParty().isLeader(player))
			{
				showChatWindow(player, 3);
				return;
			}

			/* TODO раскомментировать
			if(player.getParty().getMemberCount() < 7)
			{
				showChatWindow(player, 3);
				return;
			}
			*/

			if(st.hasMoreTokens())
			{
				Coliseum coliseum = UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel());
				if(coliseum == null)
				{
					showChatWindow(player, 3);
					return;
				}

				if(coliseum.getWaitingPartys().size() > 4)
				{
					showChatWindow(player, 3);
					return;
				}

				for(L2Player member : player.getParty().getPartyMembers())
				{
					if(member.getLevel() > getMaxLevel() || member.getLevel() < getMinLevel())
					{
						player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
						return;
					}
					if(member.isCursedWeaponEquipped())
					{
						player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
						return;
					}
				}

				Coliseum.register(player, getMinLevel(), getMaxLevel());
				return;
			}
			else
				Log.add("Wrong data or cheater? try to register whithout lvl", "Coliseum");
		}
		else if(actualCommand.startsWith("view"))
		{
			int count = 0;
			String filename = "data/html/Coliseum/" + 5 + ".htm";
			NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, 5);

			Coliseum coliseum = UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel());
			if(coliseum != null)
				for(L2Party team : coliseum.getWaitingPartys())
					if(team != null)
					{
						if(count == 0)
							html.replace("%Team1%", team.getPartyLeader().getName());
						else if(count == 1)
							html.replace("%Team2%", team.getPartyLeader().getName());
						else if(count == 2)
							html.replace("%Team3%", team.getPartyLeader().getName());
						else if(count == 3)
							html.replace("%Team4%", team.getPartyLeader().getName());
						else if(count == 4)
							html.replace("%Team5%", team.getPartyLeader().getName());
						count++;
						if(count > 5)
						{
							Log.add("We have six or more registred clans to UC WTF?", "UC");
							continue;
						}
					}

			if(count == 0)
			{
				html.replace("%Team1%", "none");
				html.replace("%Team2%", "none");
				html.replace("%Team3%", "none");
				html.replace("%Team4%", "none");
				html.replace("%Team5%", "none");
			}

			player.sendPacket(html);
		}
		//TODO: диалог
		else if(actualCommand.startsWith("winner"))
		{
			String filename;
			NpcHtmlMessage html;

			/*			if(UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners() != null)
						{
							filename = "data/html/Coliseum/"+ 7 + "htm";
							html = new NpcHtmlMessage(player, this, filename, 7);
							html.replace("winner", UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners().getPartyLeader().getName());
						}
						else
						{*/
			filename = "data/html/Coliseum/" + 6 + ".htm";
			html = new NpcHtmlMessage(player, this, filename, 6);
			//			}

			player.sendPacket(html);
		}
		else if(actualCommand.startsWith("Multisell") || actualCommand.startsWith("multisell"))
		{
			int listId = Integer.parseInt(command.substring(9).trim());
			Castle castle = getCastle(player);
			L2Multisell.getInstance().SeparateAndSend(listId, player, castle != null ? castle.getTaxRate() : 0);
		}
		else
			super.onBypassFeedback(player, command);
	}
}