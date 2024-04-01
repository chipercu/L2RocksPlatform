package com.fuzzy.subsystem.gameserver.communitybbs.Manager;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Files;

public class FailBBSManager extends BaseBBSManager
{
	public static FailBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}

	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if (command.equals("_bbsbash;"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "10.htm", player);
			content = content.replace("%name%", player.getName());
			separateAndSend(content, player);
		}
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
	}

	private static class SingletonHolder
	{
		protected static final FailBBSManager _instance = new FailBBSManager();
	}
}